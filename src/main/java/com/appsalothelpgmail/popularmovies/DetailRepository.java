package com.appsalothelpgmail.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.Network.JSONUtils;
import com.appsalothelpgmail.popularmovies.Network.NetworkUtils;
import com.appsalothelpgmail.popularmovies.Network.TMDbValues;

import org.json.JSONObject;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DetailRepository {
    private static DetailRepository mInstance;
    private static String TAG = DetailRepository.class.getSimpleName();

    private static Context mContext;

    private LiveData<MovieObject> mCurrentMovieObject;
    private LiveData<List<MovieObject>> mMovieObjects;


    public static DetailRepository getInstance(Context context){
        if(mInstance != null){
            return mInstance;
        } else {
            mInstance = new DetailRepository(context);
        }
        return mInstance;
    }

    public DetailRepository(Context context){
        //Empty constructor
    }


    public void getSingleMovie(Context context, int id, String STATE){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieObject movie = null;
                Log.d(TAG, "Running in background");
                if (MovieDatabase.getInstance(context).movieDao().existsInDatabase(id) && STATE.equals(MainActivity.STATE_FAVORITE)) {
                    //Pull from database
                    mCurrentMovieObject = getMovieObjectFromDatabase(context, id);

                    Log.d(TAG, "Retrieved object");

                } else if (STATE.equals(MainActivity.STATE_NETWORK)) {
                    //Pull from network
                    LiveData<MovieObject> movieObject = getMovieObjectFromNetwork(context, id);
                    mCurrentMovieObject = movieObject;
                } else{
                    Log.e(TAG, "Error: cannot retrieve movie");
                }




                Activity activityContext = (Activity) context;
                activityContext.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Log.d(TAG, "Alerting DetailViewModel");
                        DetailViewModel.setMovieObject(mCurrentMovieObject);
                    }
                });
            }
        });

    }

    private LiveData<MovieObject> getMovieObjectFromDatabase(Context context, int id){
        MovieObject movieObjectLiveData = MovieDatabase.getInstance(context).movieDao().queryMovie(id);
        return new MutableLiveData<>(movieObjectLiveData);
    }

    private LiveData<MovieObject> getMovieObjectFromNetwork(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            requestQueue.add(jsonObjectRequest);


            try {
                JSONObject object = future.get();
                MovieObject movieObject = JSONUtils.parseSingleJSONAsLiveData(object).getValue();
                movieObject.setReviews(getReviews(context, id));
                movieObject.setVideos(getVideos(context, id));
                return new MutableLiveData<>(movieObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Log.w(TAG, "No internet");

        Log.d(TAG, "getMovieObject returning null");
        return null;
    }

    private String[] getReviews(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_REVIEWS_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

            try {
                JSONObject result = future.get();
                return objectArrToStringArr(JSONUtils.parseReviews(result).toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Log.w(TAG, "No internet");


        return null;
    }

    private String[] objectArrToStringArr(Object[] objects){
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++){
            strings[i] = objects[i].toString();
        }

        return strings;
    }

    private String[] getVideos(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_VIDEO_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);


            try {
                JSONObject result = future.get();
                return objectArrToStringArr(JSONUtils.parseVideos(result).toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Log.w(TAG, "No internet");


        return null;
    }


}

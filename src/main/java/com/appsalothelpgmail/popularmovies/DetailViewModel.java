package com.appsalothelpgmail.popularmovies;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.Network.JSONUtils;
import com.appsalothelpgmail.popularmovies.Network.NetworkUtils;
import com.appsalothelpgmail.popularmovies.Network.TMDbValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    private static final String TAG = DetailViewModel.class.getSimpleName();
    private LiveData<MovieObject> mMovieObject;


    @Nullable
    private LiveData<ArrayList<String>> mReviews = null;
    @Nullable
    private LiveData<ArrayList<String>> mVideos = null;

    /*
    * Pass in null for @param db to pull object from web
    * */
    public DetailViewModel(MovieDatabase db, int movieId, Context context){
        if(db == null){
            setReviews(context, movieId);
            setVideos(context, movieId);
            MutableLiveData<MovieObject> objectMutableLiveData = new MutableLiveData<>();
            objectMutableLiveData.setValue( new MovieObject(234, "Rand title", "Release Date", "plotSummary", new String[]{"reviews"}, new String[]{"videos"}, "voteAverage", "https://equusmagazine.com/.image/t_share/MTQ1Mjc2NDE3MzE2NzU5MDA5/picture-2-771636.png"));
            mMovieObject = objectMutableLiveData;
        } else mMovieObject = db.movieDao().queryMovie(movieId);


    }

    public LiveData<MovieObject> getMovieObject(){
        return mMovieObject;
    }

//    private void setMovieObject(Context context, int id){
//        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_REVIEWS_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
//        if(NetworkUtils.isOnline()) {
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        mReviews = JSONUtils.parseVideos(response);
//                        Log.d(TAG, mReviews.getValue().toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d(TAG, JSONUtils.parseJSON(response).toString());
//                }
//            }, error -> error.printStackTrace());
//
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            requestQueue.add(jsonObjectRequest);
//        } else Log.w(TAG, "No internet");
//
//
//    }

    private void setReviews(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_REVIEWS_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        mReviews = JSONUtils.parseReviews(response);
                        Log.d(TAG, mReviews.getValue().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, JSONUtils.parseJSON(response).toString());
                }
            }, error -> error.printStackTrace());

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } else Log.w(TAG, "No internet");


    }

    private void setVideos(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_VIDEO_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        mVideos = JSONUtils.parseVideos(response);
                        Log.d(TAG, mVideos.getValue().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, JSONUtils.parseJSON(response).toString());
                }
            }, error -> error.printStackTrace());

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } else Log.w(TAG, "No internet");


    }


//    public static LiveData<MovieObject> callURL(Context context){
//        final LiveData<MovieObject>[] objects = new LiveData[]{null};
//        String url = TMDbValues.TMDB_BASE_URL + TMDbValues.TMDB_POPULAR + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
//        if(NetworkUtils.isOnline()) {
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        objects[0] = JSONUtils.parseSingleJSONAsLiveData(response);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, error -> error.printStackTrace());
//
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            requestQueue.add(jsonObjectRequest);
//        } else {
//            Log.w(TAG, "No internet");
//        }
//        return objects[0];
//    }

}

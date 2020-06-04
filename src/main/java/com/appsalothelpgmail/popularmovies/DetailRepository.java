package com.appsalothelpgmail.popularmovies;

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

public class DetailRepository {
    private static DetailRepository mInstance;
    private static String TAG = DetailRepository.class.getSimpleName();


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


    public MovieObject getSingleMovie(Context context, int id, String STATE){
        MovieObject movie = null;
        if (MovieDatabase.getInstance(context).movieDao().existsInDatabase(id) && STATE.equals(MainActivity.STATE_FAVORITE)) {
            //Pull from database
            movie = getMovieObjectFromDatabase(context, id);

        } else if (STATE.equals(MainActivity.STATE_NETWORK)) {
            //Pull from network
            movie = getMovieObjectFromNetwork(context, id);;
        } else{
            Log.e(TAG, "Error: cannot retrieve movie");
        }

        return movie;
    }

    private MovieObject getMovieObjectFromDatabase(Context context, int id){
        return MovieDatabase.getInstance(context).movieDao().queryMovie(id);
    }

    private MovieObject getMovieObjectFromNetwork(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

            try {
                JSONObject object = future.get();
                MovieObject movieObject = JSONUtils.parseSingleJSON(object);
                movieObject.setReviews(getReviews(context, id));
                movieObject.setVideos(getVideos(context, id));
                return movieObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Log.w(TAG, "No internet");

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

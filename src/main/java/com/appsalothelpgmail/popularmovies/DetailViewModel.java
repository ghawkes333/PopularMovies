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
    private MutableLiveData<MovieObject> mMovieObject;


    @Nullable
    private LiveData<ArrayList<String>> mReviews = null;
    @Nullable
    private LiveData<ArrayList<String>> mVideos = null;

    /*
    * Pass in null for @param db to pull object from web
    * */
    public DetailViewModel(MovieDatabase db, int movieId, Context context){
        if(db == null){
            Log.d(TAG, "DB is null. Setting objects from network");
            mMovieObject = new MutableLiveData<>();
            mMovieObject.setValue(new MovieObject(-1, "", "", "", new String[] {}, new String[] {}, "", ""));
            setMovieObject(context, movieId);
        } else {
            mMovieObject.setValue(db.movieDao().queryMovie(movieId).getValue());
        };


    }

    public LiveData<MovieObject> getMovieObject(){
        return mMovieObject;
    }

    private void setMovieObject(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        MovieObject object = JSONUtils.parseSingleJSONAsLiveData(response).getValue();
                        setReviews(context, id, object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                error.printStackTrace();
                Log.e(TAG, "Error: Network response is " + error.networkResponse.statusCode);
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            requestQueue.add(jsonObjectRequest);
        } else Log.w(TAG, "No internet");


    }

    private void setReviews(Context context, int id, MovieObject object){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_REVIEWS_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        object.setReviews(objectArrToStringArr(JSONUtils.parseReviews(response).toArray()));
                        setVideos(context, id, object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                error.printStackTrace();
                Log.e(TAG, "Error: Network response is " + error.networkResponse.statusCode);
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } else Log.w(TAG, "No internet");


    }

    private String[] objectArrToStringArr(Object[] objects){
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++){
            strings[i] = objects[i].toString();
        }

        return strings;
    }

    private void setVideos(Context context, int id, MovieObject object){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_VIDEO_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        object.setVideos(objectArrToStringArr(JSONUtils.parseVideos(response).toArray()));
                        mMovieObject.setValue(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                error.printStackTrace();
                Log.e(TAG, "Error: Network response is " + error.networkResponse.statusCode);
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } else Log.w(TAG, "No internet");


    }

}

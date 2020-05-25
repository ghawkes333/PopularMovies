package com.appsalothelpgmail.popularmovies.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appsalothelpgmail.popularmovies.MovieObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import androidx.lifecycle.LiveData;


public class NetworkUtils {
    private final static String TAG = NetworkUtils.class.getSimpleName();

    //Taken from https://stackoverflow.com/a/27312494
    //May 5, 2020
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;
    }


    public static JSONObject callURL(Context context, int id, String param){
        final JSONObject[] objects = new JSONObject[]{null};
        String url;
        if(id == -1) {
            url = TMDbValues.TMDB_BASE_URL + param + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        } else {
            url = TMDbValues.TMDB_BASE_URL + id + param + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        }
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    objects[0] = response;
                }
            }, error -> error.printStackTrace());

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } else {
            Log.w(TAG, "No internet");
        }
        return objects[0];
    }

    public static LiveData<String[]> getReviews(Context context, int id){
        JSONObject object = callURL(context, id, TMDbValues.TMDB_REVIEWS_PARAM);
        LiveData<String[]> reviews = null;
        try{
            reviews = JSONUtils.parseReviews(object);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return reviews;
    }

    public static LiveData<String[]> getVideos(Context context, int id){
        JSONObject object = callURL(context, id, TMDbValues.TMDB_VIDEO_PARAM);
        LiveData<String[]> videos = null;
        try{
            videos = JSONUtils.parseVideos(object);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return videos;
    }

    public static List<MovieObject> getMoviesByPopular(Context context){
        JSONObject object = callURL(context, -1, TMDbValues.TMDB_POPULAR);
        return JSONUtils.parseJSON(object);
    }

    public static List<MovieObject> getMoviesByTopRated(Context context){
        JSONObject object = callURL(context, -1, TMDbValues.TMDB_TOP_RATED);
        return JSONUtils.parseJSON(object);
    }
}

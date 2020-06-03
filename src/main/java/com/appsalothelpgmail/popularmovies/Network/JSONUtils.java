package com.appsalothelpgmail.popularmovies.Network;


import android.util.Log;

import com.appsalothelpgmail.popularmovies.MovieObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class JSONUtils {

    public static List<MovieObject> parseJSON(JSONObject object){
        Log.d("JSONUtils", "Running parseJSON");
        ArrayList<MovieObject> objects = new ArrayList<MovieObject>();
        try{
            JSONArray results = object.getJSONArray(TMDbValues.TMDB_RESPONSE_RESULTS);
            for(int i = 0; i < results.length(); i++) {
                objects.add(parseSingleJSON(results.getJSONObject(i)));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return objects;
    }

    public static LiveData<List<MovieObject>> parseJSONAsLiveData(JSONObject object){
        Log.d("JSONUtils", "Running parseJSONAsLiveData");

        MutableLiveData<ArrayList<MovieObject>> objects = new MutableLiveData<>();
        try{
            JSONArray results = object.getJSONArray(TMDbValues.TMDB_RESPONSE_RESULTS);
            ArrayList<MovieObject> objectsArray = new ArrayList<>();
            objects.postValue(objectsArray);

            for(int i = 0; i < results.length(); i++) {
                objectsArray.add(parseSingleJSON(results.getJSONObject(i)));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return (LiveData) objects;
    }

    public static MovieObject parseSingleJSON(JSONObject object) throws JSONException{
        Log.d("JSONUtils", "Running parseSingleJSONAsLiveData");

        int id;

        String title = object.getString(TMDbValues.TMDB_RESPONSE_TITLE);
        String overview = object.getString(TMDbValues.TMDB_RESPONSE_PLOT);
        String releaseDate = object.getString(TMDbValues.TMDB_RESPONSE_RELEASE_DATE);
        String voteAverage = object.getString(TMDbValues.TMDB_RESPONSE_VOTE_AVERAGE);

        id = object.getInt(TMDbValues.TMDB_RESPONSE_MOVIE_ID);

        String imageURL = TMDbValues.TMDB_IMAGE_URL + object.getString(TMDbValues.TMDB_RESPONSE_MOVIE_IMAGE_PATH);

        MovieObject movieObject = new MovieObject(id, title, releaseDate, overview, null, null, voteAverage, imageURL);
        return movieObject;
    }

    public static ArrayList<String> parseReviews(JSONObject object) throws JSONException{
        JSONArray array = object.getJSONArray(TMDbValues.TMDB_RESPONSE_RESULTS);
        ArrayList<String> reviews = new ArrayList<String>();
        for(int i = 0; i < array.length(); i++){
            Log.d("JSONUtils", array.getJSONObject(i).toString());
            reviews.add(array.getJSONObject(i).getString(TMDbValues.TMDB_RESPONSE_CONTENT));

        }


        return reviews;
    }

    public static ArrayList<String> parseVideos(JSONObject object) throws JSONException{
        JSONArray array = object.getJSONArray(TMDbValues.TMDB_RESPONSE_RESULTS);
        ArrayList<String> videos = new ArrayList<String>();
        for(int i = 0; i < array.length(); i++){
            if(array.getJSONObject(i).getString(TMDbValues.TMDB_RESPONSE_SITE).equals("YouTube")) {
                videos.add(TMDbValues.YOUTUBE_BASE_URL + array.getJSONObject(i).getString(TMDbValues.TMDB_RESPONSE_KEY));
            }
        }

        return videos;
    }
}

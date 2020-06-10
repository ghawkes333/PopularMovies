package com.appsalothelpgmail.popularmovies.network;


import com.appsalothelpgmail.popularmovies.service.model.MovieObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class JSONUtils {

    public static List<MovieObject> parseJSON(JSONObject object){
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
        String title = object.getString(TMDbValues.TMDB_RESPONSE_TITLE);
        String overview = object.getString(TMDbValues.TMDB_RESPONSE_PLOT);
        String releaseDate = object.getString(TMDbValues.TMDB_RESPONSE_RELEASE_DATE);
        String voteAverage = object.getString(TMDbValues.TMDB_RESPONSE_VOTE_AVERAGE);

        int id = object.getInt(TMDbValues.TMDB_RESPONSE_MOVIE_ID);
        String imageURL = TMDbValues.TMDB_IMAGE_URL + object.getString(TMDbValues.TMDB_RESPONSE_MOVIE_IMAGE_PATH);

        return new MovieObject(id, title, releaseDate, overview, null, null, voteAverage, imageURL);
    }

    public static ArrayList<String> parseReviews(JSONObject object) throws JSONException{
        JSONArray array = object.getJSONArray(TMDbValues.TMDB_RESPONSE_RESULTS);
        ArrayList<String> reviews = new ArrayList<String>();
        for(int i = 0; i < array.length(); i++){
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
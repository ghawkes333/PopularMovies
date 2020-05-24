package com.appsalothelpgmail.popularmovies.Network;


import com.appsalothelpgmail.popularmovies.MovieObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private static MovieObject parseSingleJSON(JSONObject object) throws JSONException{
        int id;

        String title = object.getString(TMDbValues.TMDB_RESPONSE_TITLE);
        String overview = object.getString(TMDbValues.TMDB_RESPONSE_PLOT);
        String releaseDate = object.getString(TMDbValues.TMDB_RESPONSE_RELEASE_DATE);
        String voteAverage = object.getString(TMDbValues.TMDB_RESPONSE_VOTE_AVERAGE);

        id = object.getInt(TMDbValues.TMDB_RESPONSE_MOVIE_ID);

        String data = title + "\n" + id + "\n" + releaseDate + "\n" + voteAverage + "\n" + overview;
        String imageURL = TMDbValues.TMDB_IMAGE_URL + object.getString(TMDbValues.TMDB_RESPONSE_MOVIE_IMAGE_PATH);

        return new MovieObject(id, title, releaseDate, null, null, null, voteAverage, imageURL);
    }
}

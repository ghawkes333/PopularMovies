package com.appsalothelpgmail.popularmovies.Network;


import com.appsalothelpgmail.popularmovies.MovieObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

    public static MovieObject[] parseJSON(JSONObject object){
        MovieObject[] objects = null;
        try{
            JSONArray results = object.getJSONArray(TMDbValues.TMDB_RESPONSE_RESULTS);
            objects = new MovieObject[results.length()];
            for(int i = 0; i < results.length(); i++) {
                objects[i] = parseSingleJSON(results.getJSONObject(i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return objects;
    }

    private static MovieObject parseSingleJSON(JSONObject object) throws JSONException{
        int id;

        String title = "Title: " + object.getString(TMDbValues.TMDB_RESPONSE_TITLE);
        String overview = "Plot Overview: " + object.getString(TMDbValues.TMDB_RESPONSE_PLOT);
        String releaseDate = "Release Date: " + object.getString(TMDbValues.TMDB_RESPONSE_RELEASE_DATE);
        String voteAverage = "Vote Average: " + object.getString(TMDbValues.TMDB_RESPONSE_VOTE_AVERAGE);

        id = object.getInt(TMDbValues.TMDB_RESPONSE_MOVIE_ID);

        String data = title + "\n" + id + "\n" + releaseDate + "\n" + voteAverage + "\n" + overview;
        String imageURL = TMDbValues.TMDB_IMAGE_URL + object.getString(TMDbValues.TMDB_RESPONSE_MOVIE_IMAGE_PATH);

        return new MovieObject(data, imageURL);
    }
}

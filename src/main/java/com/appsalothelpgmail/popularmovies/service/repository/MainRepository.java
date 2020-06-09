package com.appsalothelpgmail.popularmovies.service.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.appsalothelpgmail.popularmovies.States;
import com.appsalothelpgmail.popularmovies.network.JSONUtils;
import com.appsalothelpgmail.popularmovies.network.NetworkUtils;
import com.appsalothelpgmail.popularmovies.network.TMDbValues;
import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;

import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.List;

import androidx.lifecycle.LiveData;

//Note: Should be used in a background thread
public class MainRepository {
    private static MainRepository mInstance;
    private static String TAG = MainRepository.class.getSimpleName();

    private LiveData<List<MovieObject>> mMovieObjects;

    private MainRepository() {

    }


    public static MainRepository getInstance(){
        if(mInstance != null){
            return mInstance;
        } else {
            mInstance = new MainRepository();
        }
        return mInstance;
    }

    public LiveData<List<MovieObject>> getMovieObjects(){
        return mMovieObjects;
    }

    public LiveData<List<MovieObject>> setMovieObjects(String state, MovieDatabase db, Context context, String sort){
        if(state.equals(States.STATE_FAVORITE)){
            mMovieObjects = getMovieObjectsFromDatabase(db);
            Log.d(TAG, "Getting movies from DB");
            return mMovieObjects;
        } else if(state.equals(States.STATE_NETWORK)) {
            Log.d(TAG, "Getting movies from network");
            mMovieObjects = getMovieObjectsFromNetwork(context, sort);
            return mMovieObjects;
        } else {
            throw new InvalidParameterException("State is neither 'favorite' or 'network'");
        }
    }

    private LiveData<List<MovieObject>> getMovieObjectsFromDatabase(MovieDatabase db){
        LiveData<List<MovieObject>> movieObjects = db.movieDao().queryEntireDatabase();
        return movieObjects;
    }

    private LiveData<List<MovieObject>> getMovieObjectsFromNetwork(Context context, String sort){
        String url = TMDbValues.TMDB_BASE_URL + sort + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        LiveData<List<MovieObject>> movieObjects = null;

        if(NetworkUtils.isOnline()) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            requestQueue.add(jsonObjectRequest);



            try {
                JSONObject object = future.get();
                movieObjects = JSONUtils.parseJSONAsLiveData(object);
                return movieObjects;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Log.w(TAG, "No internet");

        return movieObjects;
    }

}

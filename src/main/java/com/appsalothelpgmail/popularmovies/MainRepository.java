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

import java.security.InvalidParameterException;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//Note: Should be used in a background thread
public class MainRepository {
    private static MainRepository mInstance;
    private static String TAG = MainRepository.class.getSimpleName();

    private LiveData<List<MovieObject>> mMovieObjects;

    private MainRepository() {

    }


    static MainRepository getInstance(){
        if(mInstance != null){
            return mInstance;
        } else {
            mInstance = new MainRepository();
        }
        return mInstance;
    }


    LiveData<List<MovieObject>> getMovieObjects(String state, MovieDatabase db, Context context, String sort){
        if(state.equals(MainActivity.STATE_FAVORITE)){
            mMovieObjects = getMovieObjectsFromDatabase(db);
            return mMovieObjects;
        } else if(state.equals(MainActivity.STATE_NETWORK)) {
            mMovieObjects = getMovieObjectsFromNetwork(context, sort);
            return mMovieObjects;
        } else {
            throw new InvalidParameterException("State is neither 'favorite' or 'network'");
        }
    }

    private LiveData<List<MovieObject>> getMovieObjectsFromDatabase(MovieDatabase db){
        List<MovieObject> movieObjects = db.movieDao().queryEntireDatabase();
        return new MutableLiveData<>(movieObjects);
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

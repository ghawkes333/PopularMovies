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

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private LiveData<List<MovieObject>> mMovieObjects;
    private MovieDatabase mDb;
    private String TAG = MainViewModel.class.getSimpleName();

    /*
    * Set @param db to null to pull movie objects from the API
    *
    * */
    public MainViewModel(MovieDatabase db, Context context, String sort) {
        Log.d(TAG, "Bug 1: Starting viewmodel");
        if(db == null) {
            Log.d(TAG, "Bug 1: DB is not null. Pulling from DB...");
            mDb = db;
            mMovieObjects = db.movieDao().queryEntireDatabase();
        } else {
            Log.d(TAG, "Bug 1: DB is null. Start setMovieObject");
            setMovieObject(context, sort);
        }
    }

    public LiveData<List<MovieObject>> getMovieObjects(){
        return mMovieObjects;
    }

    private void setMovieObject(Context context, String sort){
        Log.d(TAG, "Bug 1: starting setMovieObject");
        String url = TMDbValues.TMDB_BASE_URL + sort + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            Log.d(TAG, "Bug 1: Has internet");
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            Log.d(TAG, "Bug 1: Started future. About to start request.");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);

            Log.d(TAG, "Bug 1: About to start requestQueue");

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            Log.d(TAG, "Bug 1: About to add");

            requestQueue.add(jsonObjectRequest);
            Log.d(TAG, "Bug 1: Added");
            try {
                Log.d(TAG, "Bug 1: About to get JSON");
                JSONObject object = (JSONObject) future.get();
                Log.d(TAG, "Bug 1: About to parse JSON");
                mMovieObjects = JSONUtils.parseJSONAsLiveData(object);
                Log.d(TAG, "Bug 1: JSON parsed, mMovieObjects set");
            } catch (Exception e){
                e.printStackTrace();
            }

        } else Log.w(TAG, "No internet");


    }


}

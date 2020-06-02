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
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private MovieDatabase mDb;
    private String TAG = MainViewModel.class.getSimpleName();
    private MutableLiveData<List<MovieObject>> mNetworkMovieObjects;
    private MutableLiveData<List<MovieObject>> mDatabaseMovieObjects;

    /*
    * Set @param db to null to pull movie objects from the API
    *
    * */
    public MainViewModel(MovieDatabase db, Context context, String sort) {



        Log.d(TAG, "Bug 1: Starting viewmodel");
        if(db != null) {
            Log.d(TAG, "Bug 1: DB is not null. Pulling from DB...");
            mDb = db;
            initDatabaseMovieObjects(db);
        } else {
            Log.d(TAG, "Bug 1: DB is null. Start setMovieObject");
            setMovieObject(context, sort);
        }
    }

    private void initDatabaseMovieObjects(MovieDatabase db){
        mDatabaseMovieObjects = new MutableLiveData<>();
        mDatabaseMovieObjects.postValue(db.movieDao().queryEntireDatabase());
    }

    public LiveData<List<MovieObject>> getMovieObjects(String state, MovieDatabase db, Context context, String sort){
        if(state.equals(MainActivity.STATE_FAVORITE)){
            if(mDatabaseMovieObjects == null){
                initDatabaseMovieObjects(db);
                return mDatabaseMovieObjects;
            } else return mDatabaseMovieObjects;
        } else if(state.equals(MainActivity.STATE_NETWORK)) {
            if(mNetworkMovieObjects == null){
                setMovieObject(context, sort);
                //Note: might return null here if setMovieObject uses background thread
                return mNetworkMovieObjects;
            } else return mNetworkMovieObjects;
        } else {
            throw new InvalidParameterException("State is neither 'favorite' or 'network'");
        }
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
                mNetworkMovieObjects.postValue(JSONUtils.parseJSON(object));
                Log.d(TAG, "Bug 1: JSON parsed, mMovieObjects set");
                Log.d(TAG, "Bug 1: first title is " + mNetworkMovieObjects.getValue().get(0).getTitle());
            } catch (Exception e){
                e.printStackTrace();
            }

        } else Log.w(TAG, "No internet");


    }

    public void resetMovieObjects(Context context, String sort) {
        setMovieObject(context, sort);
    }

    public void setDb(MovieDatabase mDb) {
        this.mDb = mDb;
    }



}

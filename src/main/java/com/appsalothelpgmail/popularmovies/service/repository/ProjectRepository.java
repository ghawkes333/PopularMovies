package com.appsalothelpgmail.popularmovies.service.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.appsalothelpgmail.popularmovies.States;
import com.appsalothelpgmail.popularmovies.network.JSONUtils;
import com.appsalothelpgmail.popularmovies.network.NetworkUtils;
import com.appsalothelpgmail.popularmovies.network.TMDbValues;
import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class ProjectRepository {

    private String mState;
    private String mSort;

    private static MediatorLiveData<List<MovieObject>> mMediatorMovieObjects;

    private static ProjectRepository mInstance;

    private String TAG = ProjectRepository.class.getSimpleName();

    synchronized public static ProjectRepository getInstance(){
        if(mInstance != null){
            return mInstance;
        } else {
            mInstance = new ProjectRepository();
            mMediatorMovieObjects = new MediatorLiveData<>();
        }
        return mInstance;
    }

    private ProjectRepository(){
        //Empty constructor
    }

    public LiveData<List<MovieObject>> getMovieObjectList(Context context, String state, String sort){
        if(state.equals(States.STATE_FAVORITE)){
            getMovieObjectListFromDatabase(context);
        } else if(state.equals(States.STATE_NETWORK)){
            getMovieObjectListFromNetwork(context, sort);
        } else throw new InvalidParameterException("The state is neither favorite nor network");

        return mMediatorMovieObjects;
    }


    private void getMovieObjectListFromDatabase(Context context){
        LiveData<List<MovieObject>> movieObjectsDb = MovieDatabase.getInstance(context).movieDao().queryEntireDatabase();

        mMediatorMovieObjects.addSource(movieObjectsDb, movieObjects -> {
            mMediatorMovieObjects.setValue(movieObjects);
        });
    }

    private void getMovieObjectListFromNetwork(Context context, String sort){
        String url = TMDbValues.TMDB_BASE_URL + sort + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        MutableLiveData<List<MovieObject>> networkMovieList = new MutableLiveData<>();
        ArrayList<MovieObject> list = new ArrayList<>();
        list.add(new MovieObject());
        networkMovieList.setValue(list);

        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, response -> {
                List<MovieObject> movieObjects = JSONUtils.parseJSON(response);
                networkMovieList.setValue(movieObjects);
            }, Throwable::printStackTrace);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

        } else Log.w(TAG, "No internet");

        mMediatorMovieObjects.addSource(networkMovieList, movieObjects -> {
            mMediatorMovieObjects.setValue(movieObjects);
        });
    }




    public LiveData<MovieObject> getSingleMovie(Context context, int id, String STATE){
        LiveData<MovieObject> movie = null;
        if (MovieDatabase.getInstance(context).movieDao().existsInDatabase(id) && STATE.equals(States.STATE_FAVORITE)) {
            //Pull from database
            movie = getMovieObjectFromDatabase(context, id);
        } else if (STATE.equals(States.STATE_NETWORK)) {
            //Pull from network
            movie = getMovieObjectFromNetwork(context, id);
        } else{
            Log.e(TAG, "Error: cannot retrieve movie");
        }

        return movie;
    }

    private LiveData<MovieObject> getMovieObjectFromDatabase(Context context, int id){
        return MovieDatabase.getInstance(context).movieDao().queryMovie(id);
    }

    private LiveData<MovieObject> getMovieObjectFromNetwork(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        MovieObject movieObject = null;
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        MovieObject object;
                        object = JSONUtils.parseSingleJSON(response);
                        object.setReviews(getReviews(context, id));
                        object.setVideos(getVideos(context, id));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

        } else Log.w(TAG, "No internet");
        return new MutableLiveData<>(movieObject);
    }

    private String[] getReviews(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_REVIEWS_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

            try {
                JSONObject result = future.get();
                return objectArrToStringArr(JSONUtils.parseReviews(result).toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Log.w(TAG, "No internet");


        return null;
    }

    private String[] objectArrToStringArr(Object[] objects){
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++){
            strings[i] = objects[i].toString();
        }

        return strings;
    }

    private String[] getVideos(Context context, int id){
        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_VIDEO_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        if(NetworkUtils.isOnline()) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, future, future);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);


            try {
                JSONObject result = future.get();
                return objectArrToStringArr(JSONUtils.parseVideos(result).toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Log.w(TAG, "No internet");


        return null;
    }
}

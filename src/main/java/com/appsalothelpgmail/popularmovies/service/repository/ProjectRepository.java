package com.appsalothelpgmail.popularmovies.service.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.appsalothelpgmail.popularmovies.AppExecutors;
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
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class ProjectRepository {

    private static MediatorLiveData<List<MovieObject>> mMediatorMovieObjectsList;
    private static MediatorLiveData<MovieObject> mMediatorMovieObject;

    private static ProjectRepository mInstance;

    private String TAG = ProjectRepository.class.getSimpleName();

    synchronized public static ProjectRepository getInstance(){
        if(mInstance != null){
            return mInstance;
        } else {
            mInstance = new ProjectRepository();
            mMediatorMovieObjectsList = new MediatorLiveData<>();
            mMediatorMovieObject = new MediatorLiveData<>();
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

        return mMediatorMovieObjectsList;
    }


    private void getMovieObjectListFromDatabase(Context context){
        LiveData<List<MovieObject>> movieObjectsDb = MovieDatabase.getInstance(context).movieDao().queryEntireDatabase();

        mMediatorMovieObjectsList.addSource(movieObjectsDb, movieObjects -> {
            mMediatorMovieObjectsList.setValue(movieObjects);
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

        mMediatorMovieObjectsList.addSource(networkMovieList, movieObjects -> {
            mMediatorMovieObjectsList.setValue(movieObjects);
        });
    }




    public LiveData<MovieObject> getSingleMovie(Context context, int id, String STATE){
        if (STATE.equals(States.STATE_FAVORITE)) {
            //Pull from database
            getMovieObjectFromDatabase(context, id);
        } else if (STATE.equals(States.STATE_NETWORK)) {
            //Pull from network
            getMovieObjectFromNetwork(context, id);
        } else{
            Log.e(TAG, "Error: cannot retrieve movie");
        }

        return mMediatorMovieObject;
    }

    private void getMovieObjectFromDatabase(Context context, int id){
        mMediatorMovieObject.addSource(MovieDatabase.getInstance(context).movieDao().queryMovie(id), (movieObject -> {
            mMediatorMovieObject.setValue(movieObject);
        }));
    }

    private void getMovieObjectFromNetwork(Context context, int id){
        //Start a background thread
        AppExecutors.getInstance().networkIO().execute(() -> {
            //Check if online
            if(NetworkUtils.isOnline()) {
                //Prepare to get movie details
                String detailsUrl = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
                RequestFuture<JSONObject> detailsFuture = RequestFuture.newFuture();
                JsonObjectRequest detailsRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, detailsUrl, null, detailsFuture, detailsFuture);

                //Prepare to get movie reviews
                String reviewsUrl = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_REVIEWS_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
                RequestFuture<JSONObject> reviewsFuture = RequestFuture.newFuture();
                JsonObjectRequest reviewsRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, reviewsUrl, null, reviewsFuture, reviewsFuture);

                //Prepare to get movie trailers
                String videoUrl = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_VIDEO_PARAM + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
                RequestFuture<JSONObject> videoFuture = RequestFuture.newFuture();
                JsonObjectRequest videoRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, videoUrl, null, videoFuture, videoFuture);


                //Add all requests to queue
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(detailsRequest);
                requestQueue.add(reviewsRequest);
                requestQueue.add(videoRequest);

                try {
                    //Get each response
                    JSONObject detailsResponse = detailsFuture.get();
                    JSONObject reviewsResponse = reviewsFuture.get();
                    JSONObject videoResponse = videoFuture.get();

                    //Add to a movieObject
                    MovieObject object;
                    object = JSONUtils.parseSingleJSON(detailsResponse);
                    object.setReviews(objectArrToStringArr(JSONUtils.parseReviews(reviewsResponse).toArray()));
                    object.setVideos(objectArrToStringArr(JSONUtils.parseVideos(videoResponse).toArray()));

                    //Set global movie object
                    mMediatorMovieObject.postValue(object);
                } catch (InterruptedException | ExecutionException | JSONException e) {
                    e.printStackTrace();
                }

            } else Log.w(TAG, "No internet");

        });

    }

    private String[] objectArrToStringArr(Object[] objects){
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++){
            strings[i] = objects[i].toString();
        }

        return strings;
    }
}

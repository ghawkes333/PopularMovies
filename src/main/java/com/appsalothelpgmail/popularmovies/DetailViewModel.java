package com.appsalothelpgmail.popularmovies;

import android.content.Context;
import android.util.Log;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    private static final String TAG = DetailViewModel.class.getSimpleName();
    private static MutableLiveData<MovieObject> mMovieObject;


    @Nullable
    private LiveData<ArrayList<String>> mReviews = null;
    @Nullable
    private LiveData<ArrayList<String>> mVideos = null;

    /*
    * Pass in null for @param db to pull object from web
    * */
    public DetailViewModel(MovieDatabase db, int movieId, Context context, String state){
        //Get the movie
        DetailRepository.getInstance(context).getSingleMovie(context, movieId, state);
        //While MovieRepository is pulling the movie from the background, create a placeholder movieObject
        MovieObject object = new MovieObject(-1, null, null, null, null, null, null, null);
        mMovieObject = new MutableLiveData<>();
        mMovieObject.postValue(object);

    }

    public LiveData<MovieObject> getMovieObject(){
        return mMovieObject;
    }

    public static void setMovieObject(LiveData<MovieObject> movieObject){
        Log.d(TAG, "Setting movie object");
        mMovieObject.postValue(movieObject.getValue());
        Log.d(TAG, "Movieobject title is " + movieObject.getValue().getTitle());
    }

}

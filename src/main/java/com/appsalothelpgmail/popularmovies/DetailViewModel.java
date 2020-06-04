package com.appsalothelpgmail.popularmovies;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    private static LiveData<MovieObject> mMovieObject;


    public DetailViewModel(MovieDatabase db, int movieId, Context context, String state){
        //Get the movie
        DetailRepository.getInstance(context).getSingleMovie(context, movieId, state);
        //While MovieRepository is pulling the movie from the background, create a placeholder movieObject
        MovieObject object = DetailRepository.getInstance(context).getSingleMovie(context, movieId, state);
        mMovieObject = new MutableLiveData<>(object);
    }

    public LiveData<MovieObject> getMovieObject(){
        return mMovieObject;
    }

}

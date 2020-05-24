package com.appsalothelpgmail.popularmovies;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    private LiveData<MovieObject> mMovieObject;

    public DetailViewModel(MovieDatabase db, int movieId){
        mMovieObject = db.movieDao().queryMovie(movieId);
    }

    public LiveData<MovieObject> getMovieObject(){
        return mMovieObject;
    }
}

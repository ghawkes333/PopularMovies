package com.appsalothelpgmail.popularmovies;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private LiveData<List<MovieObject>> movieObjects;
    private MovieDatabase mDb;

    public MainViewModel(MovieDatabase db) {
        mDb = db;
        movieObjects = db.movieDao().queryEntireDatabase();
    }

    public LiveData<List<MovieObject>> getMovieObjects(){
        return movieObjects;
    }

}

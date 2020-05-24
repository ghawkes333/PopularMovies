package com.appsalothelpgmail.popularmovies;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MovieDatabase mDb;
    private LiveData<List<MovieObject>> mMovie;

    public MovieViewModelFactory(MovieDatabase db){
        mDb = db;
        mMovie = db.movieDao().queryEntireDatabase();

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainViewModel(mDb);
    }
}

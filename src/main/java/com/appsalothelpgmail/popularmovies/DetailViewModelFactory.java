package com.appsalothelpgmail.popularmovies;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MovieDatabase mDb;
    private int mId;

    public DetailViewModelFactory(MovieDatabase db, int id){
        mDb = db;
        mId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailViewModel(mDb, mId);
    }
}

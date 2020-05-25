package com.appsalothelpgmail.popularmovies;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MovieDatabase mDb;
    private int mId;
    private Context mContext;

    public DetailViewModelFactory(MovieDatabase db, int id, Context context){
        mDb = db;
        mId = id;
        mContext = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailViewModel(mDb, mId, mContext);
    }
}

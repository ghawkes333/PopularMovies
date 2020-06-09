package com.appsalothelpgmail.popularmovies.viewmodel;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MovieDatabase mDb;
    private int mId;
    private Context mContext;
    private String mState;


    public DetailViewModelFactory(MovieDatabase db, int id, Context context, String state){
        mDb = db;
        mId = id;
        mContext = context;
        mState = state;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailViewModel(mDb, mId, mContext, mState);
    }
}

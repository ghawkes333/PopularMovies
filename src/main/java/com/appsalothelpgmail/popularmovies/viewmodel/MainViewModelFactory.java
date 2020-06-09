package com.appsalothelpgmail.popularmovies.viewmodel;

import android.content.Context;
import android.util.Log;

import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static final String TAG = MainViewModelFactory.class.getSimpleName();
    private MovieDatabase mDb;
    private Context mContext;
    private String mSort;
    private String mState;

    public MainViewModelFactory(MovieDatabase db, Context context, String sort, String state){
        mDb = db;
        mContext = context;
        mSort = sort;
        mState = state;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Log.d(TAG, "Bug 1: About to get JSON");
        //noinspection unchecked
        return (T) new MainViewModel(mDb, mContext, mSort, mState);
    }




}

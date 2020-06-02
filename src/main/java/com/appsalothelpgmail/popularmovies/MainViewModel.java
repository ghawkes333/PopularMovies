package com.appsalothelpgmail.popularmovies;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private MovieDatabase mDb;
    private String TAG = MainViewModel.class.getSimpleName();
    private MutableLiveData<List<MovieObject>> mNetworkMovieObjects;
    private MutableLiveData<List<MovieObject>> mDatabaseMovieObjects;

    private LiveData<List<MovieObject>> mMovieObjects;


    public MainViewModel(MovieDatabase db, Context context, String sort, String state) {

        mMovieObjects = MainRepository.getInstance().getMovieObjects(state, db, context, sort);
    }

    public LiveData<List<MovieObject>> getMovieObjects(){
        return mMovieObjects;
    }


    public void setDb(MovieDatabase mDb) {
        this.mDb = mDb;
    }



}

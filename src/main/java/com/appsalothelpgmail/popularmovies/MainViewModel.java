package com.appsalothelpgmail.popularmovies;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<List<MovieObject>> mMovieObjects;

    private String mSort;
    private String mState;
    private MovieDatabase mDb;
    private Context mContext;


    public MainViewModel(MovieDatabase db, Context context, String sort, String state) {
        mDb = db;
        mContext = context;
        mSort = sort;
        mState = state;

        mMovieObjects = new MutableLiveData<>();
        mMovieObjects.postValue(MainRepository.getInstance().getMovieObjects(mState, mDb, mContext, mSort).getValue());
    }

    public LiveData<List<MovieObject>> getMovieObjects(){
        return mMovieObjects;
    }


    public void setSort(String sort){
        mSort = sort;
    }

    public void setState(String state){
        mState = state;
    }

    public void resetMovieObjects(){
        mMovieObjects.postValue(MainRepository.getInstance().getMovieObjects(mState, mDb, mContext, mSort).getValue());
    }



}

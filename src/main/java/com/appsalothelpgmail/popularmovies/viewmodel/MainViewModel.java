package com.appsalothelpgmail.popularmovies.viewmodel;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;
import com.appsalothelpgmail.popularmovies.service.repository.ProjectRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private LiveData<List<MovieObject>> mMovieObjects;

    private String mSort;
    private String mState;
    private Context mContext;

    private static final String TAG = MainViewModel.class.getSimpleName();


    public MainViewModel(MovieDatabase db, Context context, String sort, String state) {
        mContext = context;
        mSort = sort;
        mState = state;

        setUpMovieObjects();
    }

    public LiveData<List<MovieObject>> getMovieObjects(){
        return mMovieObjects;
    }


    public void setSort(String sort){
        mSort = sort;
        setUpMovieObjects();
    }

    public void setState(String state){
        mState = state;
        setUpMovieObjects();
    }

    private void setUpMovieObjects(){
        mMovieObjects = ProjectRepository.getInstance().getMovieObjectList(mContext, mState, mSort);
    }



}

package com.appsalothelpgmail.popularmovies.viewmodel;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;
import com.appsalothelpgmail.popularmovies.service.repository.ProjectRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    private static LiveData<MovieObject> mMovieObject;


    public DetailViewModel(MovieDatabase db, int movieId, Context context, String state){
        LiveData<MovieObject> object = ProjectRepository.getInstance().getSingleMovie(context, movieId, state);
        mMovieObject = object;
    }

    public LiveData<MovieObject> getMovieObject(){
        return mMovieObject;
    }

}
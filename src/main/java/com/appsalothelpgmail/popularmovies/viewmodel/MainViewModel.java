package com.appsalothelpgmail.popularmovies.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.appsalothelpgmail.popularmovies.service.repository.MainRepository;
import com.appsalothelpgmail.popularmovies.R;
import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

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
        List<MovieObject> blank = new ArrayList<>();
        blank.add(new MovieObject(-1, null, null, null, null, null, null, ""));
        mMovieObjects.postValue(blank);
//        mMovieObjects.postValue(MainRepository.getInstance().getMovieObjects().getValue());

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
        LiveData<List<MovieObject>> movieObjects = MainRepository.getInstance().getMovieObjects();
        if(movieObjects != null)
            mMovieObjects.postValue(movieObjects.getValue());
        else {
            //No internet
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            });
        }
    }



}

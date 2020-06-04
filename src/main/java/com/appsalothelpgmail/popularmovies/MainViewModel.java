package com.appsalothelpgmail.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

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
        LiveData<List<MovieObject>> movieObjects = MainRepository.getInstance().getMovieObjects(mState, mDb, mContext, mSort);
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

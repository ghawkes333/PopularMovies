package com.appsalothelpgmail.popularmovies;

import android.content.Context;
import android.util.Log;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static final String TAG = MainViewModelFactory.class.getSimpleName();
    private MovieDatabase mDb;
    private LiveData<List<MovieObject>> mMovies = null;
    private Context mContext;
    private String mSort;

    public MainViewModelFactory(MovieDatabase db, Context context, String sort){
        Log.d(TAG, "Bug 1: In factory constructor");
        Log.d(TAG, "Bug 1: DB is " + db + "\nContext is " + context + "\nsort is " + sort);
        mDb = db;
//        if(db != null) mMovies = db.movieDao().queryEntireDatabase();
//        else setMovieObject(context, sort);
        mContext = context;
        mSort = sort;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Log.d(TAG, "Bug 1: About to get JSON");
        //noinspection unchecked
        return (T) new MainViewModel(mDb, mContext, mSort);
    }




}

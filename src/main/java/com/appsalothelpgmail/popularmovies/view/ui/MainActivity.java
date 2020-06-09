package com.appsalothelpgmail.popularmovies.view.ui;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.appsalothelpgmail.popularmovies.R;
import com.appsalothelpgmail.popularmovies.States;
import com.appsalothelpgmail.popularmovies.network.TMDbValues;
import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;
import com.appsalothelpgmail.popularmovies.view.adapter.MovieAdapter;
import com.appsalothelpgmail.popularmovies.viewmodel.MainViewModel;
import com.appsalothelpgmail.popularmovies.viewmodel.MainViewModelFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener{

    private String TAG = MainActivity.class.getSimpleName();

    private static List<MovieObject> mMovieData;

    private RecyclerView mMovieRecycler;
    private MovieAdapter mMovieAdapter;
    private MovieDatabase mDb;

    public static String CURRENT_STATE = States.STATE_FAVORITE;
    private Menu mMenu;
    private MainViewModel mViewModel;
    private Observer<List<MovieObject>> mObserver;

    private boolean isObserved = false;

    private String STATE_BUNDLE_KEY = "state";
    private String SORT_BUNDLE_KEY = "sort";

    //The current sort criteria, either Popular or Top Rated
    private String CURRENT_SORT = TMDbValues.TMDB_TOP_RATED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the current state and sort
        if(savedInstanceState != null){
            String state = savedInstanceState.getString(STATE_BUNDLE_KEY);
            String sort = savedInstanceState.getString(SORT_BUNDLE_KEY);
            if(state != null) CURRENT_STATE = state;
            if(sort != null) CURRENT_SORT = sort;
        }

        mDb = MovieDatabase.getInstance(this.getApplicationContext());
        mMovieAdapter = new MovieAdapter(mMovieData, this);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getNumColumns());

        mMovieRecycler = findViewById(R.id.rv_movie);

        mMovieRecycler.setLayoutManager(gridLayoutManager);
        mMovieRecycler.setHasFixedSize(true);
        mMovieRecycler.setAdapter(mMovieAdapter);
        setUpLiveData();
    }

    private void setUpLiveData(){
        setUpViewModel();
        //Set up observer
        observeViewModel();
    }

    private void observeViewModel(){
        mObserver = movieObjects -> {
            mMovieData = movieObjects;
            mMovieAdapter.setMovieData(mMovieData);
        };
        if(mViewModel != null && mViewModel.getMovieObjects() != null && mViewModel.getMovieObjects().hasObservers()){
            mViewModel.getMovieObjects().removeObservers(MainActivity.this);
        }
        mViewModel.getMovieObjects().observe(MainActivity.this, mObserver);
    }

    private void setUpViewModel(){
        MainViewModelFactory factory = new MainViewModelFactory(mDb, getApplicationContext(), CURRENT_SORT, CURRENT_STATE);
        mViewModel = new ViewModelProvider(MainActivity.this, factory).get(MainViewModel.class);
    }



    private int getNumColumns(){
        int width = getScreenWidth();
        int columnWidth = (int) getResources().getDimension(R.dimen.grid_item_length);
        return (int) Math.floor(width / columnWidth);
    }




    /*Adapted from https://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions-as-pixels-in-android
    *April 27, 2020
    */
    private int getScreenWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    @Override
    public void onMovieItemClick(int clickedItemIndex) {
        //Go to DetailActivity
        int id = mMovieData.get(clickedItemIndex).getId();
        Intent intentToDetails = new Intent(MainActivity.this, DetailActivity.class);
        intentToDetails.putExtra("KEY", id);
        intentToDetails.putExtra(Intent.EXTRA_TEXT, CURRENT_STATE);
        startActivity(intentToDetails);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mn_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.mn_sort:
                //Switch sort
                if(CURRENT_SORT.equals(TMDbValues.TMDB_POPULAR)) CURRENT_SORT = TMDbValues.TMDB_TOP_RATED;
                else CURRENT_SORT = TMDbValues.TMDB_POPULAR;

                setSortMenuTitle(CURRENT_SORT, item);

                mViewModel.setSort(CURRENT_SORT);
                break;
            case R.id.mn_switch_mode:
                //Set flags
                if(CURRENT_STATE.equals(States.STATE_FAVORITE)){
                    CURRENT_STATE = States.STATE_NETWORK;
                    item.setTitle(R.string.menu_show_favorites);
                    mMenu.getItem(1).setVisible(true);
                } else {
                    CURRENT_STATE = States.STATE_FAVORITE;
                    item.setTitle(R.string.menu_show_all);
                    mMenu.getItem(1).setVisible(false);
                }

                mViewModel.setState(CURRENT_STATE);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSortMenuTitle(String sort, MenuItem item){
        int titleId;
        if(sort.equals(TMDbValues.TMDB_POPULAR)) titleId = R.string.sort_menu_title_pop;
        else titleId = R.string.sort_menu_title_top;

        item.setTitle(titleId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        if(CURRENT_STATE.equals(States.STATE_NETWORK)){
            mMenu.getItem(1).setVisible(true);
            mMenu.getItem(2).setTitle(R.string.menu_show_favorites);
        } else {
            mMenu.getItem(1).setVisible(false);
            mMenu.getItem(2).setTitle(R.string.menu_show_all);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMovieAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_BUNDLE_KEY, CURRENT_STATE);
        outState.putString(SORT_BUNDLE_KEY, CURRENT_SORT);
    }
}




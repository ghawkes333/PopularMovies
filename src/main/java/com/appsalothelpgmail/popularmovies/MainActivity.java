package com.appsalothelpgmail.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.Network.TMDbValues;

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
    private static List<MovieObject> mFavoriteMovies;
    private static List<MovieObject> mNetworkMovies;

    private RecyclerView mMovieRecycler;
    private MovieAdapter mMovieAdapter;
    private MovieDatabase mDb;
    public static final String STATE_FAVORITE = "favorite";
    public static final String STATE_NETWORK = "network";
    public static String CURRENT_STATE = STATE_FAVORITE;
    private Menu mMenu;

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
        Log.d(TAG, "State is " + CURRENT_STATE);

        mDb = MovieDatabase.getInstance(this);
        mMovieAdapter = new MovieAdapter(mMovieData, this);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getNumColumns());

        mMovieRecycler = (RecyclerView) findViewById(R.id.rv_movie);

        mMovieRecycler.setLayoutManager(gridLayoutManager);
        mMovieRecycler.setHasFixedSize(true);
        mMovieRecycler.setAdapter(mMovieAdapter);

        setUpLiveData();
    }

    private void setUpLiveData(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MainViewModelFactory factory;
                if(CURRENT_STATE.equals(STATE_FAVORITE)){
                    factory = new MainViewModelFactory(mDb, null, null);
                } else{
//                    setUpDataFromNetwork(getURL());
                    Log.d(TAG, "Bug 1: Initating factory...");

                    factory = new MainViewModelFactory(null, MainActivity.this, CURRENT_SORT);
                }
                Log.d(TAG, "Bug 1: Factory initated. Initating provider...");
                MainViewModel viewModel = new ViewModelProvider(MainActivity.this, factory).get(MainViewModel.class);
//                MainViewModel mainViewModel = new MainViewModel(mDb, MainActivity.this, CURRENT_SORT);
                Log.d(TAG, "Bug 1: Provider intitated. Setting observe");
                runOnUiThread(() -> viewModel.getMovieObjects(CURRENT_STATE, mDb, MainActivity.this, CURRENT_SORT).observe(MainActivity.this, new Observer<List<MovieObject>>() {
                    @Override
                    public void onChanged(List<MovieObject> movieObjects){
                        Log.d(TAG, "Bug 1: MovieObjects changed");
                        mMovieData = movieObjects;
                        Log.d(TAG, "Bug 1: MovieObject[0] title is " + movieObjects.get(0).getTitle().toString());
                        mMovieAdapter.setMovieData(mMovieData);
                        Log.d(TAG, "Bug 1: MovieObjects changed. Data reset");

                    }
                }

                ));

            }
        });

    }



    private int getNumColumns(){
        int width = getScreenWidth();
        int columnWidth = (int) getResources().getDimension(R.dimen.grid_item_length);
        return (int) Math.floor(width / columnWidth);
    }




    //Adapted from https://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions-as-pixels-in-android
    //April 27, 2020
    private int getScreenWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }






    @Override
    public void onMovieItemClick(int clickedItemIndex) {
        Log.d("MainActivity", mMovieData.toString());

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
                if(CURRENT_SORT.equals(TMDbValues.TMDB_POPULAR)) CURRENT_SORT = TMDbValues.TMDB_TOP_RATED;
                else CURRENT_SORT = TMDbValues.TMDB_POPULAR;

                setSortMenuTitle(CURRENT_SORT, item);
                setUpLiveData();
                break;
            case R.id.mn_switch_mode:
                if(CURRENT_STATE.equals(STATE_FAVORITE)){
                    CURRENT_STATE = STATE_NETWORK;
                    item.setTitle(R.string.menu_show_favorites);
                    mMenu.getItem(1).setVisible(true);
                    setUpLiveData();
                } else {
                    CURRENT_STATE = STATE_FAVORITE;
                    item.setTitle(R.string.menu_show_all);
                    mMenu.getItem(1).setVisible(false);
                    setUpLiveData();
                }
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
        if(CURRENT_STATE.equals(STATE_NETWORK)){
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




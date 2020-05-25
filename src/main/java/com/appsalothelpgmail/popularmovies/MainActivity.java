package com.appsalothelpgmail.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.Network.JSONUtils;
import com.appsalothelpgmail.popularmovies.Network.NetworkUtils;
import com.appsalothelpgmail.popularmovies.Network.TMDbValues;

import org.json.JSONObject;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener{

    private static List<MovieObject> mMovieData;
    private RecyclerView mMovieRecycler;
    private MovieAdapter mMovieAdapter;
    private MovieDatabase mDb;
    public static final String STATE_FAVORITE = "favorite";
    public static final String STATE_NETWORK = "network";
    public static String CURRENT_STATE;

    //The current sort criteria, either Popular or Top Rated
    private boolean isPopularSort = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = MovieDatabase.getInstance(this);
        mMovieAdapter = new MovieAdapter(mMovieData, this);
        CURRENT_STATE = STATE_FAVORITE;


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
                MainViewModelFactory factory = new MainViewModelFactory(mDb);
                MainViewModel viewModel = new ViewModelProvider(MainActivity.this, factory).get(MainViewModel.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewModel.getMovieObjects().observe(MainActivity.this, movieObjects -> {
                            Log.d("MainActivity", movieObjects.toString());
                            mMovieData = movieObjects;
                            mMovieAdapter.setMovieData(mMovieData);
                            CURRENT_STATE = STATE_FAVORITE;

                        });
                    }
                });
            }
        });

    }

    private void callURL(String url){
        if(NetworkUtils.isOnline()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mMovieData = JSONUtils.parseJSON(response);
                    mMovieAdapter.setMovieData(mMovieData);
                    CURRENT_STATE = STATE_NETWORK;
                }
            }, error -> error.printStackTrace());

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } else Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }


    private int getNumColumns(){
        int width = getScreenWidth();
        int columnWidth = (int) getResources().getDimension(R.dimen.grid_item_length);
        return (int) Math.floor(width / columnWidth);
    }

    private String getURL(){
        if (isPopularSort)
            return TMDbValues.TMDB_BASE_URL + TMDbValues.TMDB_POPULAR + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
        else
            return TMDbValues.TMDB_BASE_URL + TMDbValues.TMDB_TOP_RATED + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;

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
                isPopularSort = !isPopularSort;
                setSortMenuTitle(isPopularSort, item);
                callURL(getURL());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSortMenuTitle(boolean isPopular, MenuItem item){
        int titleId;

        if(isPopular) titleId = R.string.sort_menu_title_pop;
        else titleId = R.string.sort_menu_title_top;

        item.setTitle(titleId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMovieAdapter.notifyDataSetChanged();
    }
}




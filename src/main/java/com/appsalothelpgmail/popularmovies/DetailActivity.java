package com.appsalothelpgmail.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;
    final String TAG = DetailActivity.class.getSimpleName();

    boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        MovieDatabase db = MovieDatabase.getInstance(this);

        if(getIntent() != null) {
            if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                String state = getIntent().getStringExtra(Intent.EXTRA_TEXT);

                if (getIntent().hasExtra("KEY")) {
                    int id = getIntent().getIntExtra("KEY", -1);
                    if (id != -1 && state.equals(MainActivity.STATE_FAVORITE)) {
                        //The movie is in the database
                        DetailViewModelFactory factory = new DetailViewModelFactory(db, id, DetailActivity.this);
                        DetailViewModel model = new ViewModelProvider(this, factory).get(DetailViewModel.class);
                        model.getMovieObject().observe(this, new Observer<MovieObject>() {
                            @Override
                            public void onChanged(MovieObject movieObject) {
                                populateUI(movieObject, db);
                                model.getMovieObject().removeObserver(this);
                            }
                        });


                    } else if(state.equals(MainActivity.STATE_NETWORK)){
                        //The movie is not in the database
                        DetailViewModelFactory factory = new DetailViewModelFactory(null, id, DetailActivity.this);
                        DetailViewModel model = new ViewModelProvider(this, factory).get(DetailViewModel.class);
                        model.getMovieObject().observe(this, new Observer<MovieObject>() {
                            @Override
                            public void onChanged(MovieObject movieObject) {
                                populateUI(movieObject, db);
                                model.getMovieObject().removeObserver(this);
                            }
                        });
                    } else {
                        Log.w(TAG, "The ID passed in the intent is invalid");
                    }
                }
            } else{
                Log.w(TAG, "Intent did not pass a state");
            }
        }

    }

//    private void setupFromFavorites(MovieDatabase db, int id){
//
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                LiveData<MovieObject> object = db.movieDao().queryMovie(id);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        DetailViewModelFactory factory = new DetailViewModelFactory(object);
//                        DetailViewModel model = new ViewModelProvider(DetailActivity.this, factory).get(DetailViewModel.class);
//
//                        model.getMovieObject().observe(DetailActivity.this, new Observer<MovieObject>() {
//                            @Override
//                            public void onChanged(MovieObject movie) {
//                                if (movie != null) {
//                                    mBinding.starIv.setOnClickListener(view -> onFavoriteButtonClicked(movie, db));
//                                    populateUI(movie, db);
//                                    model.getMovieObject().removeObserver(this);
//                                } else {
//                                    Log.w(TAG, "The movie indicated by the intent is null");
//                                }
//                            }
//                        });
//                    }
//                });
//
//            }
//        });
//    }
//
//    private void setupFromNetwork(int id){
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                LiveData<MovieObject> object = new ViewModelProvider(DetailActivity.this, new DetailViewModelFactory()).get(DetailActivity.class);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        DetailViewModelFactory factory = new DetailViewModelFactory(object);
//                        DetailViewModel model = new ViewModelProvider(DetailActivity.this, factory).get(DetailViewModel.class);
//
//                        model.getMovieObject().observe(DetailActivity.this, new Observer<MovieObject>() {
//                            @Override
//                            public void onChanged(MovieObject movie) {
//                                if (movie != null) {
//                                    mBinding.starIv.setOnClickListener(view -> onFavoriteButtonClicked(movie, db));
//                                    populateUI(movie, db);
//                                    model.getMovieObject().removeObserver(this);
//                                } else {
//                                    Log.w(TAG, "The movie indicated by the intent is null");
//                                }
//                            }
//                        });
//                    }
//                });
//
//            }
//        });
//    }



    private void populateUI(MovieObject movie, MovieDatabase db){
        String url = movie.getImageURL();
        String title = movie.getTitle();
        String summary = movie.getPlotSummary();
        String release = movie.getReleaseDate();
        String[] reviews = movie.getReviews();
        String[] videos = movie.getVideos();
        String voteAverage = movie.getVoteAverage();

        if(title != null) mBinding.titleTv.setText(title);
        if(summary != null) mBinding.summaryTv.setText(summary);
        if(release != null) mBinding.releaseYearTv.setText(release);
        if(voteAverage != null) mBinding.voteAverageTv.setText(voteAverage);

        if (!url.isEmpty()) Picasso.get().load(url).into(mBinding.posterIv);

        //Set the Favorite button
        AppExecutors.getInstance().diskIO().execute(() -> {
            boolean isFavorite = db.movieDao().existsInDatabase(movie.getId());
            if(isFavorite) mBinding.starIv.setImageResource(R.drawable.ic_star_filled_24dp);
            else mBinding.starIv.setImageResource(R.drawable.ic_star_empty_24dp);
        });

    }

    private void onFavoriteButtonClicked(MovieObject movie, MovieDatabase db){

        if(movie != null){
            AppExecutors.getInstance().diskIO().execute(() -> {
                boolean existsInDb = db.movieDao().existsInDatabase(movie.getId());

                if(existsInDb){
                    Log.d(TAG, "Deleting ID " + movie.getId());
                    db.movieDao().deleteMovie(movie);
                    Log.d(TAG, "Existing: " + db.movieDao().existsInDatabase(movie.getId()));
                } else{
                    Log.d(TAG, "Inserting ID " + movie.getId());
                    db.movieDao().insertMovie(movie);
                    Log.d(TAG, "Existing: " + db.movieDao().existsInDatabase(movie.getId()));
                }

                runOnUiThread(() -> {
                    if(!existsInDb){
                        mBinding.starIv.setImageResource(R.drawable.ic_star_filled_24dp);
                    } else{
                        mBinding.starIv.setImageResource(R.drawable.ic_star_empty_24dp);
                    }
                });
            });

        }
    }


}

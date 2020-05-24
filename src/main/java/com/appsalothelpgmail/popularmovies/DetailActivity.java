package com.appsalothelpgmail.popularmovies;

import android.os.Bundle;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;

    boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        MovieDatabase db = MovieDatabase.getInstance(this);

        if(getIntent().hasExtra("KEY")){
            int id = getIntent().getIntExtra("KEY", -1);
            if(id != -1) {
                final LiveData<MovieObject> movieObject = db.movieDao().queryMovie(id);
                DetailViewModelFactory factory = new DetailViewModelFactory(db, id);
                DetailViewModel model = new ViewModelProvider(this, factory).get(DetailViewModel.class);

                model.getMovieObject().observe(this, new Observer<MovieObject>() {
                    @Override
                    public void onChanged(MovieObject movie) {
                        model.getMovieObject().removeObserver(this);
                        mBinding.starIv.setOnClickListener(view -> onFavoriteButtonClicked(movie, db, id));
                        populateUI(movie);

                    }
                });


            }
        }


    }

    private void populateUI(MovieObject movie){
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

    }

    public void onFavoriteButtonClicked(MovieObject movie, MovieDatabase db, int id){

        if(movie != null){
            AppExecutors.getInstance().diskIO().execute(() -> {
                boolean existsInDb = db.movieDao().existsInDatabase(movie.getId());

                if(existsInDb){
                    db.movieDao().deleteMovie(movie);
                } else{
                    db.movieDao().insertMovie(movie);
                }

                runOnUiThread(() -> {
                    if(existsInDb){
                        mBinding.starIv.setImageResource(R.drawable.ic_star_filled_24dp);
                    } else{
                        mBinding.starIv.setImageResource(R.drawable.ic_star_empty_24dp);
                    }
                });
            });

        }
    }


}

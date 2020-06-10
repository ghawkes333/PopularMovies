package com.appsalothelpgmail.popularmovies.view.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsalothelpgmail.popularmovies.AppExecutors;
import com.appsalothelpgmail.popularmovies.R;
import com.appsalothelpgmail.popularmovies.databinding.ActivityDetailBinding;
import com.appsalothelpgmail.popularmovies.service.data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;
import com.appsalothelpgmail.popularmovies.viewmodel.DetailViewModel;
import com.appsalothelpgmail.popularmovies.viewmodel.DetailViewModelFactory;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    private final String TAG = DetailActivity.class.getSimpleName();
    private DetailViewModel mModel = null;
    private MovieDatabase mDb = null;
    private int mId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mDb = MovieDatabase.getInstance(this);


        if(getIntent() != null) {
            if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                String state = getIntent().getStringExtra(Intent.EXTRA_TEXT);

                if (getIntent().hasExtra("KEY")) {
                    mId = getIntent().getIntExtra("KEY", -1);

                    DetailViewModelFactory factory = null;
                    factory = new DetailViewModelFactory(mDb, mId, DetailActivity.this, state);
                    mModel = new ViewModelProvider(DetailActivity.this, factory).get(DetailViewModel.class);


                    mModel.getMovieObject().observe(DetailActivity.this, movieObject -> {
                        populateUI(movieObject, mDb);
                    });


                    //Set up favorite button
                    mBinding.starIv.setOnClickListener(view -> onFavoriteButtonClicked(mModel.getMovieObject().getValue(), mId, mDb));

                }
            } else{
                Log.w(TAG, "Intent did not pass a state");
            }
        }

    }

    private void populateUI(MovieObject movie, MovieDatabase db){
        if(movie == null) return;
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
        if(reviews != null) setReviews(reviews);
        if(videos != null) setVideos(videos);

        if(url != null) {
            if (!url.isEmpty()) Picasso.get().load(url).into(mBinding.posterIv);
        }



        //Set the Favorite button
        AppExecutors.getInstance().diskIO().execute(() -> {
            boolean isFavorite = db.movieDao().existsInDatabase(movie.getId());
            if(isFavorite) mBinding.starIv.setImageResource(R.drawable.ic_star_filled_24dp);
            else mBinding.starIv.setImageResource(R.drawable.ic_star_empty_24dp);
        });

    }

    private void setReviews(String[] reviews){
        for(String content : reviews){
            TextView row =  (TextView) View.inflate(this, R.layout.review_layout, null);
            row.setText(content);
            mBinding.reviewsContainer.addView(row);
        }
    }

    private void setVideos(String[] videos){
        for(int i = 0; i < videos.length; i++){
            String content = videos[i];
            ViewGroup row =  (ViewGroup) getLayoutInflater().inflate(R.layout.trailer_layout, mBinding.videosContainer, true);
            ConstraintLayout constraintLayout = (ConstraintLayout) row.getChildAt(i);
            TextView textView = constraintLayout.findViewById(R.id.trailer_link_tv);
            textView.setText(content);

            //Open trailer on click
            constraintLayout.setOnClickListener(view -> {
                TextView linkTextview = view.findViewById(R.id.trailer_link_tv);
                String url = linkTextview.getText().toString();
                Uri uri = Uri.parse(url);
                Intent intent = new Intent();
                intent.setData(uri);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);

            });
        }
    }

    private void onFavoriteButtonClicked(MovieObject movie, int id, MovieDatabase db){
        //Check for a valid ID
        if(id != -1){
            AppExecutors.getInstance().diskIO().execute(() -> {
                boolean existsInDb = db.movieDao().existsInDatabase(id);

                //Add or remove from the database
                if(existsInDb){
                    db.movieDao().deleteMovie(movie);
                } else{
                    db.movieDao().insertMovie(movie);
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
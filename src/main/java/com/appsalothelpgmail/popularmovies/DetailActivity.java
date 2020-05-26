package com.appsalothelpgmail.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsalothelpgmail.popularmovies.Data.MovieDatabase;
import com.appsalothelpgmail.popularmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
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

                    DetailViewModelFactory factory = null;
                    if (id != -1 && state.equals(MainActivity.STATE_FAVORITE)) {
                        //The movie is in the database
                        factory = new DetailViewModelFactory(db, id, DetailActivity.this);

                    } else if(state.equals(MainActivity.STATE_NETWORK)){
                        //The movie is not in the database
                        factory = new DetailViewModelFactory(null, id, DetailActivity.this);

                    } else {
                        Log.w(TAG, "The ID passed in the intent is invalid");
                    }

                    if(factory != null){
                        DetailViewModel model = new ViewModelProvider(this, factory).get(DetailViewModel.class);

                        model.getMovieObject().observe(this, movieObject -> {
                            Log.d(TAG, "MovieObject title is " + movieObject.getTitle());
                            populateUI(movieObject, db);
                        });

                        //Set up favorite button
                        mBinding.starIv.setOnClickListener(view -> onFavoriteButtonClicked(model.getMovieObject().getValue(), id, db));
                    }
                }
            } else{
                Log.w(TAG, "Intent did not pass a state");
            }
        }

    }


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
            TextView textView = (TextView) constraintLayout.findViewById(R.id.trailer_link_tv);
            textView.setText(content);

            //Open trailer on click
            constraintLayout.setOnClickListener((View.OnClickListener) view -> {
                TextView linkTextview = (TextView) view.findViewById(R.id.trailer_link_tv);
                String url = linkTextview.getText().toString();
                Uri uri = Uri.parse(url);
                Log.d(TAG, uri.toString());
                Intent intent = new Intent();
                intent.setData(uri);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);

            });
        }
    }

    private void onFavoriteButtonClicked(MovieObject movie, int id, MovieDatabase db){

        if(id != -1){
            AppExecutors.getInstance().diskIO().execute(() -> {
                boolean existsInDb = db.movieDao().existsInDatabase(id);

                if(existsInDb){
                    Log.d(TAG, "Deleting ID " + id);
                    db.movieDao().deleteMovie(movie);
                    Log.d(TAG, "Existing: " + db.movieDao().existsInDatabase(id));
                } else{
                    db.movieDao().insertMovie(movie);
                    Log.d(TAG, "Inserting ID " + id);
                    Log.d(TAG, "Existing: " + db.movieDao().existsInDatabase(id));
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

//    private void insertMovieFromNetwork(int id, MovieDatabase db){
//        String url = TMDbValues.TMDB_BASE_URL + id + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
//        if(NetworkUtils.isOnline()) {
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        MovieObject object = JSONUtils.parseSingleJSONAsLiveData(response).getValue();
//                        db.movieDao().insertMovie(object);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, error -> {
//                error.printStackTrace();
//                Log.e(TAG, "Error: Network response is " + error.networkResponse.statusCode);
//            });
//
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(jsonObjectRequest);
//        } else Log.w(TAG, "No internet");
//
//
//    }


}

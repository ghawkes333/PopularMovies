package com.appsalothelpgmail.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsalothelpgmail.popularmovies.Utils.TMDbValues;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView detailTextView = (TextView) findViewById(R.id.detail_movie_tv);
        ImageView detailImageView = (ImageView) findViewById(R.id.iv_detail_movie_poster);
        if(getIntent().hasExtra(Intent.EXTRA_TEXT)){
            String data = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            detailTextView.setText(data);
        }
        Log.d("DetailActivity", getIntent().getStringExtra(TMDbValues.TMDB_RESPONSE_MOVIE_IMAGE_PATH));
        if(getIntent().hasExtra(TMDbValues.TMDB_RESPONSE_MOVIE_IMAGE_PATH)){
            String url = getIntent().getStringExtra(TMDbValues.TMDB_RESPONSE_MOVIE_IMAGE_PATH);
            Picasso.get().load(url).into(detailImageView);
        }
    }
}

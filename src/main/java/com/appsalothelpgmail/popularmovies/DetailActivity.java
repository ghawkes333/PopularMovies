package com.appsalothelpgmail.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView detailTextView = (TextView) findViewById(R.id.detail_movie_tv);
        ImageView detailImageView = (ImageView) findViewById(R.id.iv_detail_movie_poster);
        if(getIntent().hasExtra(Intent.EXTRA_INTENT)){
            MovieObject movieObject = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);
            String data = movieObject.getData();
            detailTextView.setText(data);
            String url = movieObject.getImageURL();
            if(!url.isEmpty()) Picasso.get().load(url).into(detailImageView);
        }
    }
}

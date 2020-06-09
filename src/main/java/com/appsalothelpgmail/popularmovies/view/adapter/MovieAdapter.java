package com.appsalothelpgmail.popularmovies.view.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsalothelpgmail.popularmovies.R;
import com.appsalothelpgmail.popularmovies.service.model.MovieObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private final String TAG = MovieAdapter.class.toString();
    private List<MovieObject> mMovieData;
    private MovieItemClickListener mMovieItemClickListener;

    public MovieAdapter (List<MovieObject> movieData, MovieItemClickListener movieItemClickListener){
        mMovieData = movieData;
        mMovieItemClickListener = movieItemClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieLayout = inflater.inflate(R.layout.grid_item, parent, false);

        MovieViewHolder movieViewHolder = new MovieViewHolder(movieLayout);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if(mMovieData.get(position).getImageURL().isEmpty()) Log.i(TAG, "Empty image URL at position" + position);
         else Picasso.get().load(mMovieData.get(position).getImageURL()).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if(mMovieData == null) return 0;
        else return mMovieData.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mMovieImageView;

        public MovieViewHolder(View viewGroup){
            super(viewGroup);

            mMovieImageView = viewGroup.findViewById(R.id.iv_list_item);
            viewGroup.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPos = getAdapterPosition();
            mMovieItemClickListener.onMovieItemClick(clickedPos);
        }
    }

    public void setMovieData(List<MovieObject> movieObjects){
        mMovieData = movieObjects;
        notifyDataSetChanged();
    }

    public interface MovieItemClickListener{
        void onMovieItemClick(int clickedItemIndex);
    }
}

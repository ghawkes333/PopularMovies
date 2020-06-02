package com.appsalothelpgmail.popularmovies;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "movies")
public class MovieObject {

    @PrimaryKey
    @ColumnInfo (name = "_ID")
    private int mId;

    private String mTitle;
    private String mImageURL;

    @Ignore
    private String mPlotSummary;
    @Ignore
    private String[] mReviews;
    @Ignore
    private String[] mVideos;
    @Ignore
    private String mVoteAverage;
    @Ignore
    private String mReleaseDate;

    @Ignore
    public MovieObject(int id, String title, String releaseDate, String plotSummary, String[] reviews, String[] videos, String voteAverage, String imageURL){
        mReleaseDate = releaseDate;
        mPlotSummary = plotSummary;
        mVideos = videos;
        mId = id;
        mVoteAverage = voteAverage;
        mReviews = reviews;
        mTitle = title;
        mImageURL = imageURL;
    }

    //For Room
    public MovieObject(int mId, String mTitle, String mImageURL){
        this.mId = mId;
        this.mTitle = mTitle;
        this.mImageURL = mImageURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPlotSummary() {
        return mPlotSummary;
    }

    public int getId() {
        return mId;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getImageURL() {
        return mImageURL;
    }


    public String[] getReviews() {
        return mReviews;
    }

    public String[] getVideos() {
        return mVideos;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public void setPlotSummary(String plotSummary) {
        mPlotSummary = plotSummary;
    }

    public void setReviews(String[] reviews) {
        mReviews = reviews;
    }

    public void setVideos(String[] videos) {
        mVideos = videos;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setImageURL(String imageURL) {
        mImageURL = imageURL;
    }


}

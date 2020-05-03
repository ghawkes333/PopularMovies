package com.appsalothelpgmail.popularmovies;

public class MovieObject {
    String mData;
    String mImageURL;

    public MovieObject(String data, String imageURL){
        mData = data;
        mImageURL = imageURL;
    }

    public String getData(){return mData;}

    public String getImageURL(){return mImageURL;}

    public void setData(String data){mData = data;}

    public void setImageURL(String imageURL){mImageURL = imageURL;}
}

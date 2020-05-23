package com.appsalothelpgmail.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieObject implements Parcelable {
    private String mData;
    private String mImageURL;

    public MovieObject(String data, String imageURL){
        mData = data;
        mImageURL = imageURL;
    }

    protected MovieObject(Parcel in) {
        mData = in.readString();
        mImageURL = in.readString();
    }

    public static final Creator<MovieObject> CREATOR = new Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel in) {
            return new MovieObject(in);
        }

        @Override
        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };

    public String getData(){return mData;}

    public String getImageURL(){return mImageURL;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mData);
        parcel.writeString(mImageURL);
    }


}

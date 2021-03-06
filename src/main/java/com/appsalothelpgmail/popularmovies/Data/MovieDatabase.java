package com.appsalothelpgmail.popularmovies.Data;

import android.content.Context;

import com.appsalothelpgmail.popularmovies.MovieObject;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MovieObject.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    public final static String DB_NAME = "movies";
    public static MovieDatabase sInstance;

    public static MovieDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class,
                        MovieDatabase.DB_NAME)
                        .build();
                return sInstance;
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
}

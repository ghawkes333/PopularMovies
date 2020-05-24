package com.appsalothelpgmail.popularmovies.Data;

import com.appsalothelpgmail.popularmovies.MovieObject;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<MovieObject>> queryEntireDatabase();

    @Query("SELECT EXISTS (SELECT * FROM movies WHERE _ID = :id)")
    boolean existsInDatabase(int id);

    @Insert
    void insertMovie(MovieObject movieObject);

    @Delete
    void deleteMovie(MovieObject movieObject);

}

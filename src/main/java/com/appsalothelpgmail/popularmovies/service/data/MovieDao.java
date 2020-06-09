package com.appsalothelpgmail.popularmovies.service.data;

import com.appsalothelpgmail.popularmovies.service.model.MovieObject;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    List<MovieObject> queryEntireDatabase();

    @Query("SELECT * FROM movies WHERE _ID = :id")
    MovieObject queryMovie(int id);

    @Query("SELECT EXISTS (SELECT * FROM movies WHERE _ID = :id)")
    boolean existsInDatabase(int id);

    @Insert
    void insertMovie(MovieObject movieObject);

    @Delete
    void deleteMovie(MovieObject movieObject);

}

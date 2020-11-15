package com.example.popularmoviesstage1.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MovieDao {


    @Query("SELECT * FROM movie ORDER BY id")
    LiveData<List<MovieEntry>> loadAllFavoriteMovies();

    @Insert
    void insertMovie(MovieEntry movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry movieEntry);

    @Delete
    void deleteMovie(MovieEntry movieEntry);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieEntry loadTaskById(int id);

    @Query("SELECT * FROM movie ORDER BY id")
    List<MovieEntry> getAllFavoriteMovies();


}

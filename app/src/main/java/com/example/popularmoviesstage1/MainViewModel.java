package com.example.popularmoviesstage1;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmoviesstage1.database.AppDatabase;
import com.example.popularmoviesstage1.database.MovieEntry;
import java.util.List;

public class MainViewModel extends AndroidViewModel {


    private LiveData<List<MovieEntry>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllFavoriteMovies();
    }

    public LiveData<List<MovieEntry>> getMovies(){
        return movies;
    }


}

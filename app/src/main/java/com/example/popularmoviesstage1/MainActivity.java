package com.example.popularmoviesstage1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmoviesstage1.database.AppDatabase;
import com.example.popularmoviesstage1.database.MovieEntry;
import com.example.popularmoviesstage1.utilities.JsonUtils;
import com.example.popularmoviesstage1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {


    private TextView mErrorDisplayTextView;
    private ProgressBar mLoadingIndicator;
    private String mMoviesJSON = "";
    private RecyclerView mMovieRecyclerView;

    // TODO: Please add your own movie db Key here. Follow instructions in the link below to do so.
    // https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true#h.7sxo8jefdfll
    public static String API_KEY = "49dab07ab4bbf1b00cd0b1d2a982ad28";

    private final String POPULAR   = "popular";
    private final String TOP_RATED = "top_rated";
    private final String FAVORITE  = "favorite";

    AppDatabase mDb;
    private String latestSortCriteria = POPULAR;
    private final String SORT_CRITERIA = "sort_criteria";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext());


        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorDisplayTextView = findViewById(R.id.tv_error_message_display);
        mMovieRecyclerView = findViewById(R.id.rv_movies_grid);



        if(savedInstanceState != null){
            latestSortCriteria = savedInstanceState.getString(SORT_CRITERIA);
        }



        // Only favorite movies are stored in the local Room DB.
        // All other movies categories are stored on dbmovie web server.
        if(latestSortCriteria.equals(FAVORITE)){
            getMovieDataFromRoomDatabase();
        }else{
            getMovieDataFromNetworkRequest();
        }



    }


    private ArrayList<String> getMovieImageTitles(String moviesJSON) {
        ArrayList<String> myMoviesImages = new ArrayList<>();

        ArrayList<MovieEntry> movieObjects = JsonUtils.parseMovieJSON(moviesJSON);
        for (MovieEntry movie : movieObjects) {
            String posterPath = movie.getPoster_path();
            myMoviesImages.add(posterPath);
        }

        return myMoviesImages;
    }

    private void getMovieDataFromRoomDatabase(){

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged( List<MovieEntry> favoriteMoviesEntries) {

                mMoviesJSON = JsonUtils.movieListToJSON(favoriteMoviesEntries);
                populateGridWithMovieImages(mMoviesJSON);

            }
        });

    }

    private void getMovieDataFromNetworkRequest() {

        URL movieSearchURL = NetworkUtils.buildUrl(API_KEY, latestSortCriteria);
        MovieQueryTask myMovieQuery = new MovieQueryTask();
        myMovieQuery.execute(movieSearchURL);
    }


    private void populateGridWithMovieImages(String moviesJSON) {

        int columns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);

        MovieAdapter mAdapter = new MovieAdapter(getMovieImageTitles(moviesJSON), this, this.getBaseContext());
        mMovieRecyclerView.setAdapter(mAdapter);
    }


    private void hideErrorMessage() {
        mErrorDisplayTextView.setVisibility(View.INVISIBLE);
    }


    private void showErrorMessage() {
        mErrorDisplayTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {

        String movieClicked = JsonUtils.getMovieJSON(mMoviesJSON, clickedItemIndex);

        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("CLICKED_JSON", movieClicked);
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();


        if (itemThatWasClickedId == R.id.action_sort_popularity) {
            latestSortCriteria = POPULAR;
            getMovieDataFromNetworkRequest();
            return true;

        } else if (itemThatWasClickedId == R.id.action_sort_rating) {
            latestSortCriteria = TOP_RATED;
            getMovieDataFromNetworkRequest();
            return true;
        } else if (itemThatWasClickedId == R.id.action_favorites) {
            latestSortCriteria = FAVORITE;
            getMovieDataFromRoomDatabase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putString(SORT_CRITERIA, latestSortCriteria);
    }







    public class MovieQueryTask extends AsyncTask<URL, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(URL... params) {
            URL movieUrl = params[0];
            String movieJSONResults = null;
            try {
                movieJSONResults = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieJSONResults;
        }

        @Override
        protected void onPostExecute(String movieSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieSearchResults != null && !movieSearchResults.equals("")) {
                hideErrorMessage();

                mMoviesJSON = movieSearchResults;
                populateGridWithMovieImages(mMoviesJSON);
            } else {
                showErrorMessage();
            }
        }
    }



}
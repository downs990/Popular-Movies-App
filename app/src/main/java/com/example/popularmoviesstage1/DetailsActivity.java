package com.example.popularmoviesstage1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmoviesstage1.database.AppDatabase;
import com.example.popularmoviesstage1.database.MovieEntry;
import com.example.popularmoviesstage1.utilities.JsonUtils;
import com.example.popularmoviesstage1.utilities.NetworkUtils;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener {

    private MovieEntry clickedMovie;
    private RecyclerView mTrailerRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorDisplayTextView;

    ArrayList<String> movieTrailerYouTubeKeys;
    AppDatabase mDb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        mDb = AppDatabase.getInstance(getApplicationContext());


        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorDisplayTextView = findViewById(R.id.tv_error_message_display);


        TextView titleTextView = findViewById(R.id.tv_movie_title);
        TextView yearTextView = findViewById(R.id.tv_year);
        TextView overviewTextView = findViewById(R.id.tv_overview);
        TextView voteAverageTextView = findViewById(R.id.tv_vote_average);
        ImageView posterImageTextView = findViewById(R.id.iv_movie);


        Gson gson = new Gson();
        Intent intent = getIntent();

        if (intent != null) {
            String clickedJSON = intent.getStringExtra("CLICKED_JSON");
            clickedMovie = gson.fromJson(clickedJSON, MovieEntry.class);
        }


        titleTextView.setText(clickedMovie.getOriginal_title());
        yearTextView.setText(clickedMovie.getRelease_date());
        overviewTextView.setText(clickedMovie.getOverview());
        voteAverageTextView.setText(clickedMovie.getVote_average() + "/10");

        String starWarsImage = "https://image.tmdb.org/t/p/original" + clickedMovie.getPoster_path();// NOTE: Always HTTPS not HTTP
        Picasso.with(this.getBaseContext())
                .load(starWarsImage)
                .into(posterImageTextView);


        mTrailerRecyclerView = findViewById(R.id.trailers_rv);
        getMovieExtrasDataJSON(clickedMovie.getId(), "videos");

    }

    private boolean movieListContains(List<MovieEntry> movieList, int searchingMovieID) {
        boolean doesListContainMovie = false;

        for (MovieEntry mEntry : movieList) {
            if (mEntry.getId() == searchingMovieID) {
                doesListContainMovie = true;
            }
        }
        return doesListContainMovie;
    }

    public void addMovieToFavorites(View view) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                final List<MovieEntry> allFavoriteMovies = mDb.movieDao().getAllFavoriteMovies();
                if (movieListContains(allFavoriteMovies, clickedMovie.getId())) {
                     mDb.movieDao().deleteMovie(clickedMovie);
                } else {
                     mDb.movieDao().insertMovie(clickedMovie);
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (movieListContains(allFavoriteMovies, clickedMovie.getId())) {
                            Toast.makeText(getApplicationContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });
    }

    public void getMovieExtrasDataJSON(int selectedMovieID, String extraProperties) {

        URL movieSearchURL = NetworkUtils.buildUrl(MainActivity.API_KEY,
                selectedMovieID + "/" + extraProperties);
        DetailsActivity.MovieDetailsQueryTask myMovieQuery = new DetailsActivity.MovieDetailsQueryTask(extraProperties);
        myMovieQuery.execute(movieSearchURL);

    }


    public void populateTrailersRecyclerView(String movieDetailsSearchResults) {

        ArrayList<String> movieTrailerNames = JsonUtils.getMovieJSONByFieldName(
                movieDetailsSearchResults, "name");
        movieTrailerYouTubeKeys = JsonUtils.getMovieJSONByFieldName(
                movieDetailsSearchResults, "key");

        int columns = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        mTrailerRecyclerView.setLayoutManager(layoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);

        TrailerAdapter mAdapter = new TrailerAdapter(movieTrailerNames, this, this.getBaseContext());
        mTrailerRecyclerView.setAdapter(mAdapter);
    }


    public void populateReviewsRecyclerView(String movieDetailsSearchResults) {
        ArrayList<String> reviewAuthorsList = JsonUtils.getMovieJSONByFieldName(
                movieDetailsSearchResults, "author");

        ArrayList<String> reviewContentList = JsonUtils.getMovieJSONByFieldName(
                movieDetailsSearchResults, "content");


        Display display = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            display = ((WindowManager) Objects.requireNonNull(getSystemService(WINDOW_SERVICE))).getDefaultDisplay();
        }
        int width = display.getWidth();
        int height = display.getHeight();


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.reviews_prompt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, (int) (height * .80)); // 80% of screen height.
        }


        RecyclerView mReviewsRecyclerView = dialog.findViewById(R.id.reviews_rv);
        TextView mEmptyMessageTextView = dialog.findViewById(R.id.empty_message_tv);

        if (reviewAuthorsList.isEmpty()) {
            mEmptyMessageTextView.setVisibility(View.VISIBLE);
            mReviewsRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyMessageTextView.setVisibility(View.GONE);
            mReviewsRecyclerView.setVisibility(View.VISIBLE);


            int columns = 1;
            GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
            mReviewsRecyclerView.setLayoutManager(layoutManager);
            mReviewsRecyclerView.setHasFixedSize(true);
            ReviewsAdapter mAdapter = new ReviewsAdapter(reviewAuthorsList, reviewContentList, null, this.getBaseContext());
            mReviewsRecyclerView.setAdapter(mAdapter);
        }

        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }

        });
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {

        String myYouTubeKey = movieTrailerYouTubeKeys.get(clickedItemIndex);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + myYouTubeKey)));
        Log.i("Video", "Video Playing....");
    }


    private void hideErrorMessage() {
        mErrorDisplayTextView.setVisibility(View.INVISIBLE);
    }


    private void showErrorMessage() {
        mErrorDisplayTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_show_reviews) {
            getMovieExtrasDataJSON(clickedMovie.getId(), "reviews");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MovieDetailsQueryTask extends AsyncTask<URL, Void, String> {

        public String reviewsOrTrailers;

        public MovieDetailsQueryTask() {
        }

        public MovieDetailsQueryTask(String reviewsOrTrailers) {
            this.reviewsOrTrailers = reviewsOrTrailers;
        }

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
        protected void onPostExecute(String movieDetailsSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);


            if (movieDetailsSearchResults != null && !movieDetailsSearchResults.equals("")) {
                hideErrorMessage();

                switch (reviewsOrTrailers) {
                    case "videos":
                        populateTrailersRecyclerView(movieDetailsSearchResults);
                        break;
                    case "reviews":
                        populateReviewsRecyclerView(movieDetailsSearchResults);
                        break;
                }


            } else {
                showErrorMessage();
            }
        }
    }


}

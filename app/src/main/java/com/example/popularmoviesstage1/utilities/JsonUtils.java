package com.example.popularmoviesstage1.utilities;

import androidx.lifecycle.LiveData;

import com.example.popularmoviesstage1.database.MovieEntry;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {


    public static String getMovieJSON(String popularMoviesJSON, int index){

        String result = "";

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(popularMoviesJSON);
            JSONArray movieObjects = (JSONArray) jsonObject.get("results");
            result = movieObjects.get(index).toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<String> getMovieJSONByFieldName(String popularMoviesJSON, String fieldName){

        ArrayList<String> result = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(popularMoviesJSON);
            JSONArray movieObjects = (JSONArray) jsonObject.get("results");

            for(int i = 0; i < movieObjects.length(); i++){
                result.add( ((JSONObject)movieObjects.get(i)).get(fieldName).toString() );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static String movieListToJSON(List<MovieEntry> movieList){
        String jsonString;

        String allMoviesJSON = "";
        for(MovieEntry movie : movieList){
            Gson myGson = new Gson();
            allMoviesJSON += myGson.toJson(movie) + ",";
        }

        jsonString = "{\"results\": [ " + allMoviesJSON + " ]}";
        return jsonString;
    }



    public static ArrayList<MovieEntry> parseMovieJSON(String myMoviesJSON){
        ArrayList<MovieEntry> newMovieList = new ArrayList<>();


        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(myMoviesJSON);
            JSONArray movieObjects = (JSONArray) jsonObject.get("results");

            Gson gson = new Gson();
            for(int i = 0; i < movieObjects.length(); i++ ){

                MovieEntry currentMovie = gson.fromJson( movieObjects.get(i).toString() , MovieEntry.class);
                newMovieList.add( currentMovie );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newMovieList;
    }


}

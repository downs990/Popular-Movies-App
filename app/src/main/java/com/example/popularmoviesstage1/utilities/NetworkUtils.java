package com.example.popularmoviesstage1.utilities;


import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class NetworkUtils {




    final static String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/movie/";

    final static String PARAM_QUERY = "api_key";

    public static URL buildUrl(String apiKey, String extraPropertiy) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + extraPropertiy).buildUpon()
                .appendQueryParameter(PARAM_QUERY, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        }catch(Exception e){
            return e.getMessage();
        } finally {
            urlConnection.disconnect();
        }
    }


}
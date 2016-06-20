/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.networking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.model.ResultMovies;
import es.alvaroweb.popularmovies.model.ResultReviews;
import es.alvaroweb.popularmovies.model.ResultVideos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * TODO: Create JavaDoc
 */
public class ApiConnection {
    private final static String URL_BASE = "https://api.themoviedb.org/";
    private final static String OPTION_POPULAR = "popular";
    private final static String OPTION_TOP_RATED = "top_rated";
    private MovieService service;
    private Retrofit retrofit;
    private Context context;
    private final String API_KEY;

    public ApiConnection(Context context) {
        this.context = context;
        API_KEY = context.getString(R.string.API_KEY);
        setConnection();
    }

    private void setConnection() {
        // configure retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MovieService.class);
    }

    /**
     * fetchs a ResultMovie JSON,
     *
     * @param page must be greater than 0
     */
    public void getMovies(int page, Callback<ResultMovies> moviesCallback) {
        if (page <= 0) return;

        Call<ResultMovies> moviesCall;

        if (isPopularMoviesSetting()) {
            moviesCall = service.getMovies(OPTION_POPULAR, API_KEY, page);
        } else {
            moviesCall = service.getMovies(OPTION_TOP_RATED, API_KEY, page);
        }

        // asynchronous call
        moviesCall.enqueue(moviesCallback);
    }

    public void getVideos(long movieId, Callback<ResultVideos> videosCallback){
        Call<ResultVideos> videosCall;
        videosCall = service.getVideos(movieId, API_KEY);
        videosCall.enqueue(videosCallback);
    }

    public void getReviews(long movieId, int page, Callback<ResultReviews> reviewsCallback){
        Call<ResultReviews> reviewsCall;
        reviewsCall = service.getReviews(movieId, API_KEY, page);
        reviewsCall.enqueue(reviewsCallback);
    }

    private boolean isPopularMoviesSetting() {

        String key = context.getString(R.string.pref_by_popular_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isPopularSetting = preferences.getBoolean(key, true);
        // change activity title
        if (!isPopularSetting) {
            String title = context.getString(R.string.top_rated_title);
            ((Activity) context).setTitle(title);
        }
        return isPopularSetting;
    }
}

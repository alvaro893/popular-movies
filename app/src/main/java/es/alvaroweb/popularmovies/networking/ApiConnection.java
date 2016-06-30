/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.networking;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.helpers.PreferencesHelper;
import es.alvaroweb.popularmovies.model.ResultMovies;
import es.alvaroweb.popularmovies.model.ResultReviews;
import es.alvaroweb.popularmovies.model.ResultVideos;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 *  handles all the stuff related with the connection to the the web API
 */
public class ApiConnection {
    private final static String URL_BASE = "https://api.themoviedb.org/";
    private final static String OPTION_POPULAR = "popular";
    private final static String OPTION_TOP_RATED = "top_rated";
    private final String API_KEY;
    private MovieService mService;
    private Context mContext;

    public ApiConnection(Context context) {
        this.mContext = context;
        API_KEY = context.getString(R.string.API_KEY);
        setConnection();
    }

    private void setConnection() {
        // configure mRetrofit
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = mRetrofit.create(MovieService.class);
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
            moviesCall = mService.getMovies(OPTION_POPULAR, API_KEY, page);
        } else {
            moviesCall = mService.getMovies(OPTION_TOP_RATED, API_KEY, page);
        }

        // asynchronous call
        moviesCall.enqueue(moviesCallback);
    }

    public void getVideos(long movieId, Callback<ResultVideos> videosCallback) {
        Call<ResultVideos> videosCall;
        videosCall = mService.getVideos(movieId, API_KEY);
        videosCall.enqueue(videosCallback);
    }

    public void getReviews(long movieId, int page, Callback<ResultReviews> reviewsCallback) {
        Call<ResultReviews> reviewsCall;
        reviewsCall = mService.getReviews(movieId, API_KEY, page);
        reviewsCall.enqueue(reviewsCallback);
    }

    private boolean isPopularMoviesSetting() {
        boolean isPopular = PreferencesHelper.readSpinnerOption(mContext) ==
                PreferencesHelper.POPULAR_SELECTION;
        return isPopular;
    }
}

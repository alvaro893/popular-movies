/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.networking;

import es.alvaroweb.popularmovies.model.ResultMovies;
import es.alvaroweb.popularmovies.model.ResultReviews;
import es.alvaroweb.popularmovies.model.ResultVideos;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Interface which handles the http requests to the
 * web API using Retrofit
 */
public interface MovieService {
    @GET("3/movie/{popular}") Call<ResultMovies> getMovies(@Path("popular") String popular,
                                                           @Query("api_key") String apiKey,
                                                           @Query("page") int page);

    @GET("3/movie/{movieId}/videos") Call<ResultVideos> getVideos(@Path("movieId") long movieId,
                                                                  @Query("api_key") String apiKey);

    @GET("3/movie/{movieId}/reviews") Call<ResultReviews> getReviews(@Path("movieId") long movieId,
                                                                     @Query("api_key") String apiKey,
                                                                     @Query("page") int page);
}

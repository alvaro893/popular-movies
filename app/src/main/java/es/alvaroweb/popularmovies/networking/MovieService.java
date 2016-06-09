/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.networking;

import es.alvaroweb.popularmovies.model.ResultMovies;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Interface which handles the http requests to the
 * web API
 */
public interface MovieService {
    @GET("3/movie/{popular}")
    Call<ResultMovies> getMovies(@Path("popular") String popular, @Query("api_key") String apiKey);
}

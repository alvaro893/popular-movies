/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import es.alvaroweb.popularmovies.model.Movie;


import static es.alvaroweb.popularmovies.data.MoviesContract.*;

/*
 * TODO: This Class uses the movie content provider to perform operations in Activities
 */
public class MovieStoreHelper {
    private static final String DEBUG_TAG = MovieStoreHelper.class.getSimpleName();
    private Context context;
    private ContentResolver resolver;

    public MovieStoreHelper(Context context) {
        this.context = context;
        resolver = context.getContentResolver();
    }

    private boolean isMovieInDb(Movie movie){
        Uri uri = MovieEntry.buildMovieUri(movie.getId());
        Cursor cursor = resolver.query(uri,
                    new String[]{MovieEntry._ID},
                    null,
                    null,
                    null);
        boolean isMovie = cursor.moveToFirst();
        cursor.close();
        return isMovie;
    }

    /**  Toggles a movie in the database.
     *   Warning: This method should be called OUT of the UI Thread **/
    public void insertMovieorDeleteMovie(Movie movie) {
        Uri uri = MovieEntry.buildMovieUri(movie.getId());

        if(isMovieInDb(movie)){
            resolver.delete(uri ,null,null);
        }else{
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            values.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            values.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            values.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());

            Uri insertedUri = resolver.insert(uri, values);
            Log.d(DEBUG_TAG, "inserted: "+insertedUri);
        }
    }


}

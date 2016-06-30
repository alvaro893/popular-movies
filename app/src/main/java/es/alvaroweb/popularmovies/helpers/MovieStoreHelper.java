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
import es.alvaroweb.popularmovies.model.ResultReviews;
import es.alvaroweb.popularmovies.model.ResultReviews.*;
import es.alvaroweb.popularmovies.model.ResultVideos;
import es.alvaroweb.popularmovies.model.ResultVideos.*;


import static es.alvaroweb.popularmovies.data.MoviesContract.*;

/*
 *  This Class uses the mMovie content provider to perform operations in Activities
 */
public class MovieStoreHelper {
    private static final String DEBUG_TAG = MovieStoreHelper.class.getSimpleName();
    private ContentResolver mResolver;
    private Movie mMovie;
    private ResultReviews mResultReviews;
    private ResultVideos mResultVideos;

    public MovieStoreHelper(Context context, Movie movie, ResultReviews resultReviews, ResultVideos resultVideos) {
        this.mResolver = context.getContentResolver();
        this.mMovie = movie;
        this.mResultReviews = resultReviews;
        this.mResultVideos = resultVideos;
    }

    private boolean isMovieInDb(Movie movie){
        Uri uri = MovieEntry.buildMovieUri(movie.getId());
        Cursor cursor = mResolver.query(uri,
                    new String[]{MovieEntry._ID},
                    null,
                    null,
                    null);
        boolean isMovie = cursor.moveToFirst();
        cursor.close();
        return isMovie;
    }

    /**  Toggles a mMovie in the database.
     *   Warning: This method should be called OUT of the UI Thread **/
    public void insertMovieorDeleteMovie() {
        Uri uri = MovieEntry.buildMovieUri(mMovie.getId());

        if(isMovieInDb(mMovie)){
            //Note: reviews and videos will be deleted by cascade
            mResolver.delete(uri ,null,null);
        }else{
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_BACKDROP_PATH, mMovie.getBackdropPath());
            values.put(MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            values.put(MovieEntry.COLUMN_POPULARITY, mMovie.getPopularity());
            values.put(MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            values.put(MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            values.put(MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());

            Uri insertedUri = mResolver.insert(uri, values);
            Log.d(DEBUG_TAG, "inserted: "+insertedUri);
            insertReviews();
            insertVideos();
        }
    }

    private void insertVideos() {
        for( final Video video: mResultVideos.getResults() ){
            Uri uri = VideoEntry.buildVideoUri();
            ContentValues values = new ContentValues();
            values.put(VideoEntry.COLUMN_KEY, video.getKey());
            values.put(VideoEntry.COLUMN_NAME, video.getName());
            values.put(VideoEntry.COLUMN_SITE, video.getSite());
            values.put(VideoEntry.COLUMN_ID_MOVIE, mMovie.getId());

            Uri result = mResolver.insert(uri,values);
            Log.d(DEBUG_TAG, "inserted " + result);
        }
    }


    private void insertReviews() {
        for( final Review review: mResultReviews.getResults() ){
            Uri uri = ReviewEntry.buildReviewUri();
            ContentValues values = new ContentValues();
            values.put(ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            values.put(ReviewEntry.COLUMN_CONTENT, review.getContent());
            values.put(ReviewEntry.COLUMN_ID_MOVIE, mMovie.getId());

            Uri result = mResolver.insert(uri,values);
            Log.d(DEBUG_TAG, "inserted " + result);
        }

    }
}

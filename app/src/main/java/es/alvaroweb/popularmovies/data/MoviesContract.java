/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/*
 * Defines tables and their column names for the database
 * Also it defines the URI for the content provider and methods to
 * build those uri and extract its parameters
 */
public class MoviesContract {
    /** content authority is the name for the content provider **/
    public static final String CONTENT_AUTHORITY = "es.alvaroweb.popularmovies.app";

    /** uri object that is the base of all uris that will is the content provider **/
    public static final Uri URI_CONTENT_BASE = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** paths available in this contract **/
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_TEST = "test";



    public final static class MovieEntry implements BaseColumns{
        /** URI to access this table **/
        public static final Uri CONTENT_URI = URI_CONTENT_BASE.buildUpon()
                .appendPath(PATH_MOVIE).build();

        /* types of contents */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        // Table name and columns
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        /** Appends a single id to the CONTENT_URI **/
        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUri() {
            return CONTENT_URI;
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public final static class ReviewEntry implements BaseColumns{
        /** URI to access this table **/
        public static final Uri CONTENT_URI = URI_CONTENT_BASE.buildUpon()
                .appendPath(PATH_REVIEW).build();

        // Uri types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        // Table name and columns
        public static final String TABLE_NAME = "review";
        public static final String COLUMN_AUTHOR ="author";
        public static final String COLUMN_CONTENT ="content";
        public static final String COLUMN_ID_MOVIE ="id_movie";

        /** Appends a single id to the CONTENT_URI **/
        public static Uri buildReviewUri(long movieId){
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
        public static Uri buildReviewUri(){
            return ContentUris.withAppendedId(CONTENT_URI, 0);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public final static class VideoEntry implements BaseColumns{
        /** URI to access this table **/
        public static final Uri CONTENT_URI = URI_CONTENT_BASE.buildUpon()
                .appendPath(PATH_VIDEO).build();

        // Uri types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        // Table name and columns
        public static final String TABLE_NAME = "video";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_ID_MOVIE = "id_movie";
        /** Appends a single id to the CONTENT_URI **/
        public static Uri buildVideoUri(long movieId){
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
        public static Uri buildVideoUri(){
            return ContentUris.withAppendedId(CONTENT_URI, 0);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public final static class TestEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                URI_CONTENT_BASE.buildUpon().appendPath(PATH_TEST).build();
        // Uri types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEST;

        // Table name and columns
        public static final String TABLE_NAME = "test";
        public static final String COLUMN_TEST = "tt";

        public static Uri buildTestUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTestColumn(String rowid){
            return CONTENT_URI.buildUpon().appendPath(rowid).build();
        }
    }
}

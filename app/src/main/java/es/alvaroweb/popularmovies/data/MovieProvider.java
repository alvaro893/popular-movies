/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


/*
 * Content provider to access the database defined in the Movies contract
 */
public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String UNKNOWN_URI = "Unknown uri: ";
    private MoviesDBHelper mOpenHelper;

    /* Every operation has a integer value to be matched using buildUriMatcher*/
    static final int MOVIES = 100;
    static final int FAVORITE_MOVIE = 101;
    static final int REVIEWS_WITH_MOVIE_ID = 300;
    static final int VIDEOS_WITH_MOVIE_ID = 400;

    /* sql helpers for queries */
    static final String favoriteSelection = MoviesContract.MovieEntry._ID + " = ?";
    static final String[] singleIdSelectionArgs(long id){
        return new String[]{String.valueOf(id)};
    }
    static final String reviewsSelection = MoviesContract.ReviewEntry.COLUMN_ID_MOVIE + " = ?";
    static final String videoSelection = MoviesContract.VideoEntry.COLUMN_ID_MOVIE + " = ?";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH); // NO_MATCH id for the root to match nothing
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIE , MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_REVIEW + "/#", REVIEWS_WITH_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_VIDEO + "/#", VIDEOS_WITH_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", FAVORITE_MOVIE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case MOVIES:{
                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case FAVORITE_MOVIE:{
                cursor = getFavoriteMovie(uri,projection,sortOrder);
                break;
            }
            case REVIEWS_WITH_MOVIE_ID:{
                cursor = getReviews(uri,projection,sortOrder);
                break;
            }
            case VIDEOS_WITH_MOVIE_ID:{
                cursor = getVideos(uri, projection,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }




    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEWS_WITH_MOVIE_ID:
                return MoviesContract.ReviewEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final String FAILED_MESSAGE = "Failed to insert row into ";
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVORITE_MOVIE:{
                long id = MoviesContract.MovieEntry.getIdFromUri(uri);
                values.put(MoviesContract.MovieEntry._ID, id);
                long returnId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (returnId > 0) {
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(returnId);
                } else {
                    throw new SQLException(FAILED_MESSAGE + uri);
                }
                break;
            }
            case REVIEWS_WITH_MOVIE_ID:{
                long returnId = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if(returnId > 0){
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(returnId);
                }else {
                    throw new SQLException(FAILED_MESSAGE + uri);
                }
                break;
            }
            case VIDEOS_WITH_MOVIE_ID:{
                long returnId = db.insert(MoviesContract.VideoEntry.TABLE_NAME, null, values);
                if (returnId > 0) {
                    returnUri = MoviesContract.VideoEntry.buildVideoUri(returnId);
                }else{
                    throw new SQLException(FAILED_MESSAGE + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
       SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        long id = MoviesContract.MovieEntry.getIdFromUri(uri);
        switch (sUriMatcher.match(uri)){
            case FAVORITE_MOVIE:{
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        favoriteSelection,
                        singleIdSelectionArgs(id));
                break;
            }
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        long id = MoviesContract.MovieEntry.getIdFromUri(uri);
        switch (sUriMatcher.match(uri)){
            case FAVORITE_MOVIE:{
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME,
                        values,
                        favoriteSelection,
                        singleIdSelectionArgs(id));
                break;
            }
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }



    private Cursor getFavoriteMovie(Uri uri, String[] projection, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        long id = MoviesContract.MovieEntry.getIdFromUri(uri);
        return db.query(
                MoviesContract.MovieEntry.TABLE_NAME,
                projection,
                favoriteSelection,
                singleIdSelectionArgs(id),
                null,
                null,
                sortOrder);
    }

    private Cursor getVideos(Uri uri, String[] projection, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        long movieId = MoviesContract.VideoEntry.getIdFromUri(uri);
        return db.query(MoviesContract.VideoEntry.TABLE_NAME,
                projection,
                videoSelection,
                singleIdSelectionArgs(movieId),
                null,
                null,
                sortOrder);
    }

    private Cursor getReviews(Uri uri, String[] projection, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        long movieId = MoviesContract.ReviewEntry.getIdFromUri(uri);
        return db.query(MoviesContract.ReviewEntry.TABLE_NAME,
                projection,
                reviewsSelection,
                singleIdSelectionArgs(movieId),
                null,
                null,
                sortOrder);
    }


}

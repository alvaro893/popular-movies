/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
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
    static final int FAVORITE_MOVIE = 100;
    static final int REVIEWS = 300;
    static final int VIDEOS = 400;

    /* helpers for FAVORITE_MOVIE queries */
    static final String favoriteSelection = MoviesContract.MovieEntry._ID + " = ?";
    static final String[] favoriteArgs(long id){
        return new String[]{String.valueOf(id)};
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH); // NO_MATCH id for the root to match nothing
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_REVIEW + "/#", REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_VIDEO + "/#", VIDEOS);
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
            case FAVORITE_MOVIE:{
                cursor = getFavoriteMovie(uri,projection,sortOrder);
                break;
            }
            case REVIEWS:{
                cursor = getReviews(uri,projection,sortOrder);
            }
            case VIDEOS:{
                cursor = getVideos(uri, projection,sortOrder);
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
            case FAVORITE_MOVIE:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
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
            case REVIEWS:{
                long returnId = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if(returnId > 0){
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(returnId);
                }else {
                    throw new SQLException(FAILED_MESSAGE + uri);
                }
                break;
            }
            case VIDEOS:{
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
                        favoriteArgs(id));
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
                        favoriteArgs(id));
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
                favoriteArgs(id),
                null,
                null,
                sortOrder);
    }

    private Cursor getVideos(Uri uri, String[] projection, String sortOrder) {
        return null;
    }

    private Cursor getReviews(Uri uri, String[] projection, String sortOrder) {
        return null;
    }


}

/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static es.alvaroweb.popularmovies.data.MoviesContract.*;
/*
 * TODO: Create JavaDoc
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "favoritemovies.db";
    static final String CONFIGURE_FOREIGN_KEYS = "PRAGMA foreign_keys = ON";

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_ID_MOVIE + " INTEGER NOT NULL REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ") ON DELETE CASCADE );";

                db.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VideoEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_ID_MOVIE + " INTEGER REFERENCES " + MovieEntry.TABLE_NAME +
                "(" + MovieEntry._ID + ") ON DELETE CASCADE);";

                db.execSQL(SQL_CREATE_VIDEO_TABLE);

        final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " + TestEntry.TABLE_NAME + " (" +
                TestEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TestEntry.COLUMN_TEST + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_TEST_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // this is necessary for cascade deletion
        db.execSQL(CONFIGURE_FOREIGN_KEYS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}

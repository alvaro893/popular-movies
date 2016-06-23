/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/*
 * TODO: Create JavaDoc
 */
public class TestDatabase extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDBHelper.DATABASE_NAME);
    }


    public void setUp(){
        deleteTheDatabase();
    }

    public void testCreateDb(){

        SQLiteDatabase db = new MoviesDBHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: database not created", c.moveToFirst());

        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: there is no info",c.moveToFirst());

        c.close();
        db.close();

    }

    public void testWriteDB(){
        SQLiteDatabase db = new MoviesDBHelper(mContext).getWritableDatabase();
        ContentValues testValues = new ContentValues();

        testValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 23.3);
        testValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "test/path");
        testValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 4.3);
        testValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "this is a plot");
        testValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "test/path/2");
        testValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, 1);
        testValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "testtitle");

        try {
            long locataionRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);
        }catch (Exception e){
            e.printStackTrace();
        }
        // check row was inserted
        //assertTrue(locataionRowId != -1);

        Cursor curosr = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: No records returned", curosr.moveToFirst());

        curosr.close();
        db.close();
    }
}

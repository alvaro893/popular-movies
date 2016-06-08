/*
 * Copyright (C) 2013 The Android Open Source Project
 */
package es.alvaroweb.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Get data
        Gson gson = new Gson();
        ResultMovies resultMovies;
        if(isPopularMoviesSetting()){
            resultMovies = gson.fromJson(RawData.popularMovies, ResultMovies.class);
        }else{
            resultMovies = gson.fromJson(RawData.topRated, ResultMovies.class);
        }

        // Set data in adapter
        MoviesAdapter moviesAdapter = new MoviesAdapter(this, resultMovies.getResults());
        GridView moviesGridView = (GridView) findViewById(R.id.movies_grid_view);
        moviesGridView.setAdapter(moviesAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isPopularMoviesSetting(){

        String key = getString(R.string.pref_by_popular_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPopularSetting = preferences.getBoolean(key, true);
        // change activity title
        if(!isPopularSetting){
            String title = getString(R.string.top_rated_title);
            setTitle(title);
        }
        return isPopularSetting;
    }
}

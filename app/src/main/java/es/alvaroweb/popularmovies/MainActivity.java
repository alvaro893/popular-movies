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
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    private final static String URL_BASE = "https://api.themoviedb.org/";
    private final static String OPTION_POPULAR = "popular";
    private final static String OPTION_TOP_RATED = "top_rated";
    private  MovieService service;
    private Retrofit retrofit;
    private ResultMovies resultMovies;
    private GridView moviesGridView;
    private MoviesAdapter moviesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Get data
        setConnection();
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

    private void setGridOfMovies(){
        moviesAdapter = new MoviesAdapter(this, resultMovies.getResults());
        moviesGridView = (GridView) findViewById(R.id.movies_grid_view);
        moviesGridView.setAdapter(moviesAdapter);
        moviesGridView.setOnItemClickListener(this);
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

    private void setConnection(){
        String API_KEY = getString(R.string.API_KEY);
        // configure retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MovieService.class);

        // do request
        Call<ResultMovies> moviesCall;
        if(isPopularMoviesSetting()){
            moviesCall = service.getMovies(OPTION_POPULAR, API_KEY);
        }else{
            moviesCall = service.getMovies(OPTION_TOP_RATED, API_KEY);
        }
        // asynchronous call
        moviesCall.enqueue(new Callback<ResultMovies>() {
            @Override
            public void onResponse(Call<ResultMovies> call, Response<ResultMovies> response) {
                Log.d(DEBUG_TAG, "response:" + response.code() + " for:" + call.request().url().toString());
                resultMovies = response.body();
                setGridOfMovies();
            }

            @Override
            public void onFailure(Call<ResultMovies> call, Throwable t) {
                Log.d(DEBUG_TAG, "error:" + t.getMessage() + " for:" + call.request().url().toString());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie selectedMovie = resultMovies.getResults().get(position);
        Intent intentDetailActivity = new Intent(this, DetailsActivity.class);

        intentDetailActivity.putExtra(getString(R.string.SELECTED_MOVIE), selectedMovie);
        startActivity(intentDetailActivity);
        Toast.makeText(this, selectedMovie.getTitle(), Toast.LENGTH_LONG).show();
    }
}

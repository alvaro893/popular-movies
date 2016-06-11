/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.moviesgrid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.details.DetailsActivity;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.model.ResultMovies;
import es.alvaroweb.popularmovies.networking.MovieService;
import es.alvaroweb.popularmovies.settings.SettingsActivity;
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
    private MovieService service;
    private Retrofit retrofit;
    private ResultMovies resultMovies;
    private MoviesAdapter moviesAdapter;
    private GridView moviesGridView;
    private FloatingActionButton leftButton;
    private FloatingActionButton rightButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Get data
        setConnection();
        doRequest(1);
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
        moviesAdapter.notifyDataSetChanged();

        leftButton = (FloatingActionButton) findViewById(R.id.prev_fab);
        rightButton = (FloatingActionButton) findViewById(R.id.next_fab);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paginate(v);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paginate(v);
            }
        });
    }

    private void paginate(View v) {
        if(v.getId() == R.id.prev_fab){
            doRequest(resultMovies.getPreviousPage());
        }else{
            doRequest(resultMovies.getNextPage());
        }
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

    /** fetchs a ResultMovie JSON,
     * @param page must be greater than 0*/
    private void doRequest(int page){
        if(page <= 0) return;

        final String API_KEY = getString(R.string.API_KEY);
        Call<ResultMovies> moviesCall;

        if(isPopularMoviesSetting()){
            moviesCall = service.getMovies(OPTION_POPULAR, API_KEY, page);
        }else{
            moviesCall = service.getMovies(OPTION_TOP_RATED, API_KEY, page);
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

    private void setConnection(){
        // configure retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MovieService.class);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie selectedMovie = resultMovies.getResults().get(position);
        Intent intentDetailActivity = new Intent(this, DetailsActivity.class);

        intentDetailActivity.putExtra(getString(R.string.SELECTED_MOVIE), selectedMovie);
        startActivity(intentDetailActivity);
    }
}

/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.moviesgrid;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.data.MoviesContract;
import es.alvaroweb.popularmovies.details.DetailsActivity;
import es.alvaroweb.popularmovies.helpers.PreferencesHelper;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.model.ResultMovies;
import es.alvaroweb.popularmovies.networking.ApiConnection;
import es.alvaroweb.popularmovies.settings.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    private static final int FAVORITE_LOADER = 0;
    private static final int DEFAULT_PAGE = 1;
    @BindView(R.id.movies_grid_view) GridView moviesGridView;
    @BindView(R.id.spinner) Spinner spinner;
    @BindArray(R.array.spinner_options) String[] spinnerOptions;
    private ResultMovies resultMovies;
    private MoviesAdapter moviesAdapter;
    private ApiConnection apiConnection;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        setSpinner();

        // Get data
        //getResultMovies(DEFAULT_PAGE);
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
    /** attaches the fetch results to the grid. NOTE: must be called asynchronously **/
    private void setGridOfMovies() {
        moviesAdapter = new MoviesAdapter(this, resultMovies.getResults());
        moviesGridView.setAdapter(moviesAdapter);
        moviesAdapter.notifyDataSetChanged();
    }

    private void paginate(View v) {
        if (v.getId() == R.id.prev_fab) {
            getResultMovies(resultMovies.getPreviousPage());
        } else {
            getResultMovies(resultMovies.getNextPage());
        }
    }

    /** executes a callback when it get the results from the network **/
    private void getResultMovies(int page){
        apiConnection = new ApiConnection(this);
        apiConnection.getMovies(page, new Callback<ResultMovies>() {
            @Override
            public void onResponse(Call<ResultMovies> call, Response<ResultMovies> response) {
                //Log.d(DEBUG_TAG, "response:" + response.code() + " for:" + call.request().url().toString());
                resultMovies = response.body();
                setGridOfMovies();
            }

            @Override
            public void onFailure(Call<ResultMovies> call, Throwable t) {
                Log.d(DEBUG_TAG, "error:" + t.getMessage() + " for:" + call.request().url().toString());
            }
        });
    }


    @OnItemClick(R.id.movies_grid_view)
    void onMovieClick(AdapterView<?> parent, View view, int position, long id) {
        Movie selectedMovie = resultMovies.getResults().get(position);
        Intent intentDetailActivity = new Intent(this, DetailsActivity.class);

        intentDetailActivity.putExtra(getString(R.string.SELECTED_MOVIE), selectedMovie);
        intentDetailActivity.putExtra(getString(R.string.IS_FAVORITE), isFavorite);
        startActivity(intentDetailActivity);
    }

    @OnClick(R.id.prev_fab)
    void clickPreviousButton(View v) {
        paginate(v);
    }

    @OnClick(R.id.next_fab)
    void clickNextButton(View v) {
        paginate(v);
    }

    @OnItemSelected(R.id.spinner)
    void clickSpinnerItem(AdapterView<?> parent, View view, int pos, long id){
        switch (pos){
            case PreferencesHelper.TOP_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.TOP_SELECTION, this);
                getResultMovies(DEFAULT_PAGE);
                setTitle(getString(R.string.top_rated_title));
                break;
            case PreferencesHelper.POPULAR_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.POPULAR_SELECTION, this);
                getResultMovies(DEFAULT_PAGE);
                setTitle(getString(R.string.app_name));
                break;
            case PreferencesHelper.FAVORITE_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.FAVORITE_SELECTION, this);
                getSupportLoaderManager().initLoader(FAVORITE_LOADER, null, this);
                break;
            default:
                break;
        }
    }

    private void setSpinner(){
        SpinnerAdapter adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,
                spinnerOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(PreferencesHelper.readSpinnerOption(this));
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id){
            case FAVORITE_LOADER:{
                Uri uri = MoviesContract.MovieEntry.buildMovieUri();
                Log.d(DEBUG_TAG, "uri: " + uri);
                setTitle(getString(R.string.favorites_title));
                return new CursorLoader(this, uri, null, null, null, null);
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        setMoviesFromDb(data);
        setGridOfMovies();
        isFavorite = true;
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
    // call it asynchronously
    public void setMoviesFromDb(Cursor cursor) {
        if(cursor.moveToFirst()){
            resultMovies = new ResultMovies();
            ArrayList<Movie> list = new ArrayList<>();
            do{
                Movie movie = new Movie();
                movie.setId(cursor.getLong(cursor.getColumnIndex(MoviesContract.MovieEntry._ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)));
                movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POPULARITY)));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)));
                movie.setVoteAverage(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                list.add(movie);
            }while (cursor.moveToNext());
            resultMovies.setResults(list);
        }else{
            return;
        }
    }
}

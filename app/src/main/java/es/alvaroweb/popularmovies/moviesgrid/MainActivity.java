/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.moviesgrid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.data.MoviesContract;
import es.alvaroweb.popularmovies.data.MoviesDBHelper;
import es.alvaroweb.popularmovies.details.DetailFragment;
import es.alvaroweb.popularmovies.details.DetailsActivity;
import es.alvaroweb.popularmovies.helpers.PreferencesHelper;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements GridFragment.callback{

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.spinner) Spinner spinner;
    @BindArray(R.array.spinner_options) String[] spinnerOptions;
    private GridFragment gridFragment;
    private boolean isTwoPaneLayout = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        setSpinner();
        // reference to static fragment
        gridFragment = (GridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.static_grid_fragment);

        if(findViewById(R.id.detail_fragment_container) != null){
            isTwoPaneLayout = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, new EmptyFragment())
                    .commit();
        }

    }

    private void replaceFragmentWithMovie(Movie movie, boolean isFavorite) {
        Bundle args = new Bundle();
        args.putSerializable(DetailFragment.SELECTED_MOVIE_ARG, movie);
        args.putBoolean(DetailFragment.IS_FAVORITE_ARG, isFavorite);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.action_delete_favorites:
                Uri uri = MoviesContract.MovieEntry.buildMovieUri();
                getContentResolver().delete(uri, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnItemSelected(R.id.spinner)
    void clickSpinnerItem(AdapterView<?> parent, View view, int pos, long id){
        switch (pos){
            case PreferencesHelper.TOP_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.TOP_SELECTION, this);
                gridFragment.fetchMoviesFromNetwork();
                setTitle(getString(R.string.top_rated_title));
                break;
            case PreferencesHelper.POPULAR_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.POPULAR_SELECTION, this);
                gridFragment.fetchMoviesFromNetwork();
                setTitle(getString(R.string.app_name));
                break;
            case PreferencesHelper.FAVORITE_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.FAVORITE_SELECTION, this);
                gridFragment.fetchMoviesFromDb();
                setTitle(getString(R.string.favorites_title));
                break;
            default:
                break;
        }
    }

    private void setSpinner(){
        SpinnerAdapter adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,
                spinnerOptions);
        // the styles of every spinner line are applied within the setDropDownViewResource
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(PreferencesHelper.readSpinnerOption(this));
    }


    @Override
    public void onMovieClick(Movie movie, boolean isFavorite) {
        if(isTwoPaneLayout){
            replaceFragmentWithMovie(movie, isFavorite);
        }else{
            Intent intentDetailActivity = new Intent(this, DetailsActivity.class);
            intentDetailActivity.putExtra(DetailFragment.SELECTED_MOVIE_ARG, movie);
            intentDetailActivity.putExtra(getString(R.string.IS_FAVORITE), isFavorite);
            startActivity(intentDetailActivity);
        }

    }
}

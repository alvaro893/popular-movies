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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.data.MoviesContract;
import es.alvaroweb.popularmovies.details.DetailFragment;
import es.alvaroweb.popularmovies.details.DetailsActivity;
import es.alvaroweb.popularmovies.helpers.PreferencesHelper;
import es.alvaroweb.popularmovies.helpers.SystemServicesHelper;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements GridFragment.callback{

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.spinner) Spinner mSpinner;
    @BindArray(R.array.spinner_options) String[] mSpinnerOptions;
    private GridFragment mGridFragment;
    private boolean mIsTwoPaneLayout = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setGridFragment();

        // no need to create fragments if state was saved
        if(savedInstanceState == null){
            setDetailFragment();
        }

        ButterKnife.bind(this);
        setSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void setDetailFragment() {
        int detailContainerId = R.id.detail_fragment_container;
        // add empty fragment if this is a two pane layout
        if(findViewById(detailContainerId) != null){
            mIsTwoPaneLayout = true;
            EmptyFragment emptyFragment =
                    EmptyFragment.newInstance(getString(R.string.to_start_click_a_movie));
            getSupportFragmentManager().beginTransaction()
                    .replace(detailContainerId, emptyFragment)
                    .commit();
        }
    }

    private void setGridFragment() {
        int gridContainerId = R.id.grid_fragment_container;
        //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        boolean isNetworkAvilable = SystemServicesHelper.isNetworkAvailable(this);
        // put empty fragment if no network connection exists
        if(isNetworkAvilable) {
            mGridFragment = new GridFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(gridContainerId, mGridFragment)
                    .commit();
        }else{
            EmptyFragment emptyFragment = EmptyFragment.newInstance(getString(R.string.no_internet_message));
            getSupportFragmentManager().beginTransaction()
                    .replace(gridContainerId, emptyFragment)
                    .commit();
        }
        getSupportFragmentManager().executePendingTransactions();
    }

    private void replaceFragmentWithMovie(Movie movie, boolean isFavorite) {
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.SELECTED_MOVIE_ARG, movie);
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
                setGridFragment();
                if(mGridFragment != null){
                    mGridFragment.fetchMoviesFromNetwork();
                }
                setTitle(getString(R.string.top_rated_title));
                break;
            case PreferencesHelper.POPULAR_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.POPULAR_SELECTION, this);
                setGridFragment();
                if(mGridFragment != null){
                    mGridFragment.fetchMoviesFromNetwork();
                }
                setTitle(getString(R.string.app_name));
                break;
            case PreferencesHelper.FAVORITE_SELECTION:
                // favorite movies must be ALWAYS available, even with no Internet
                setGridFragmentForFavorites();
                mGridFragment.fetchMoviesFromDb();
                PreferencesHelper.setSpinnerOption(PreferencesHelper.FAVORITE_SELECTION, this);
                setTitle(getString(R.string.favorites_title));
                break;
            default:
                break;
        }
    }

    private void setGridFragmentForFavorites() {
        mGridFragment = new GridFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.grid_fragment_container, mGridFragment ).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void setSpinner(){
        SpinnerAdapter adapter = new SpinnerAdapter(this,
                android.R.layout.simple_spinner_item,
                mSpinnerOptions);
        // the styles of every mSpinner line are applied within the setDropDownViewResource
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(PreferencesHelper.readSpinnerOption(this));
    }


    @Override
    public void onMovieClick(Movie movie, boolean isFavorite) {
        if(mIsTwoPaneLayout){
            replaceFragmentWithMovie(movie, isFavorite);
        }else{
            Intent intentDetailActivity = new Intent(this, DetailsActivity.class);
            intentDetailActivity.putExtra(DetailFragment.SELECTED_MOVIE_ARG, movie);
            intentDetailActivity.putExtra(getString(R.string.IS_FAVORITE), isFavorite);
            startActivity(intentDetailActivity);
        }

    }
}

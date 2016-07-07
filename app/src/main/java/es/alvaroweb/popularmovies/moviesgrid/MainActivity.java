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
    private static final String TAG_GRID_FRAGMENT = "GRIDFG";
    @BindView(R.id.spinner) Spinner mSpinner;
    @BindArray(R.array.spinner_options) String[] mSpinnerOptions;
    private GridFragment mGridFragment;
    private boolean mIsTwoPaneLayout = false;
    private boolean mSpinnerHasChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(findViewById(R.id.detail_fragment_container) != null){
            mIsTwoPaneLayout = true;
        }

        if(savedInstanceState == null){
            setDetailFragment();
            createGridFragment();
        }else{
            restoreGridFragment(savedInstanceState);
        }

        ButterKnife.bind(this);
        setSpinner();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            getSupportFragmentManager().putFragment(outState, TAG_GRID_FRAGMENT, mGridFragment);
    }

    private void restoreGridFragment(Bundle savedInstanceState) {
        mGridFragment = (GridFragment) getSupportFragmentManager()
                .getFragment(savedInstanceState, TAG_GRID_FRAGMENT);
    }

    private void createGridFragment() {
        mGridFragment = new GridFragment();
        addGridFragment();
    }

    private void addGridFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.grid_fragment_container, mGridFragment, TAG_GRID_FRAGMENT)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void setDetailFragment() {
        int detailContainerId = R.id.detail_fragment_container;
        // add empty fragment if this is a two pane layout

            EmptyFragment emptyFragment =
                    EmptyFragment.newInstance(getString(R.string.to_start_click_a_movie));
            getSupportFragmentManager().beginTransaction()
                    .replace(detailContainerId, emptyFragment)
                    .commit();
    }

    private void setEmptyFragmentOnNetworkFailure() {
        int gridContainerId = R.id.grid_fragment_container;
        boolean isNetworkAvilable = SystemServicesHelper.isNetworkAvailable(this);
        // put empty fragment if no network connection exists
        if (!isNetworkAvilable) {
            EmptyFragment emptyFragment = EmptyFragment
                    .newInstance(getString(R.string.no_internet_message));

            getSupportFragmentManager().beginTransaction()
                    .replace(gridContainerId, emptyFragment)
                    .commit();
        }
        getSupportFragmentManager().executePendingTransactions();
    }

    private void replaceFragmentWithMovie(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.SELECTED_MOVIE_ARG, movie);
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
    /** called when an option in the spinner is selected. Note: this is called automatically when the
     * view spinner is created */
    @OnItemSelected(R.id.spinner)
    void selectSpinnerItem(AdapterView<?> parent, View view, int pos, long id){
        Log.d(DEBUG_TAG, "onitemSelected"+id);
        if (mSpinnerHasChanged){
            mGridFragment.setDefaultState();
        }
        switch (pos){
            case PreferencesHelper.TOP_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.TOP_SELECTION, this);
                setGridFromInternetMovies();
                setTitle(getString(R.string.top_rated_title));
                break;
            case PreferencesHelper.POPULAR_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.POPULAR_SELECTION, this);
                setGridFromInternetMovies();
                setTitle(getString(R.string.app_name));
                break;
            case PreferencesHelper.FAVORITE_SELECTION:
                PreferencesHelper.setSpinnerOption(PreferencesHelper.FAVORITE_SELECTION, this);
                setGridFragmentForFavorites();
                break;
            default:
                break;
        }
        mSpinnerHasChanged = true;
    }

    private void setGridFromInternetMovies(){
        setEmptyFragmentOnNetworkFailure();
        mGridFragment.fetchMoviesFromNetwork();
    }

    private void setGridFragmentForFavorites() {
        if(!mGridFragment.isAdded()){
            addGridFragment();
        }
        mGridFragment.fetchMoviesFromDb();
        setTitle(getString(R.string.favorites_title));
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
    public void onMovieClick(Movie movie) {
        mSpinnerHasChanged = false;
        if(mIsTwoPaneLayout){
            replaceFragmentWithMovie(movie);
        }else{
            Intent intentDetailActivity = new Intent(this, DetailsActivity.class);
            intentDetailActivity.putExtra(DetailFragment.SELECTED_MOVIE_ARG, movie);
            startActivity(intentDetailActivity);
        }
    }
}

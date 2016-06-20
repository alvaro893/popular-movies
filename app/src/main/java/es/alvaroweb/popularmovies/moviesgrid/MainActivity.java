/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.moviesgrid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.details.DetailsActivity;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.model.ResultMovies;
import es.alvaroweb.popularmovies.model.ResultVideos;
import es.alvaroweb.popularmovies.networking.ApiConnection;
import es.alvaroweb.popularmovies.settings.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();
    private ResultMovies resultMovies;
    private MoviesAdapter moviesAdapter;
    private ApiConnection apiConnection;
    @BindView(R.id.movies_grid_view) GridView moviesGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // Get data
        getResultMovies(1);
        //delete this
        apiConnection.getVideos(550, new Callback<ResultVideos>() {
            @Override
            public void onResponse(Call<ResultVideos> call, Response<ResultVideos> response) {
                ResultVideos resultVideos = response.body();
                ResultVideos.Video video = resultVideos.getResults().get(0);
                Log.d(DEBUG_TAG, video.getName());
            }

            @Override
            public void onFailure(Call<ResultVideos> call, Throwable t) {

            }
        });
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


}

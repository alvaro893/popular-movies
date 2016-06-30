package es.alvaroweb.popularmovies.moviesgrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.data.MoviesContract;
import es.alvaroweb.popularmovies.details.DetailFragment;
import es.alvaroweb.popularmovies.details.DetailsActivity;
import es.alvaroweb.popularmovies.helpers.PreferencesHelper;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.model.ResultMovies;
import es.alvaroweb.popularmovies.networking.ApiConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment that shows the grid of movies in the main activity
 */
public class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FAVORITE_LOADER = 0;
    private static final int DEFAULT_PAGE = 1;
    private static final String DEBUG_TAG = GridFragment.class.getSimpleName();
    @BindView(R.id.movies_grid_view)
    GridView moviesGridView;

    private MoviesAdapter moviesAdapter;
    private ApiConnection apiConnection;
    private boolean isFavorite = false;
    private callback mListener;
    private Activity context;
    private ResultMovies resultMovies;

    public GridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof callback) {
            mListener = (callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FAVORITE_LOADER: {
                Uri uri = MoviesContract.MovieEntry.buildMovieUri();
                Log.d(DEBUG_TAG, "uri: " + uri);
                context.setTitle(getString(R.string.favorites_title));
                return new CursorLoader(context, uri, null, null, null, null);
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        // get out of the method if the favorite option was not selected
        final int CURRENT_OPTION = PreferencesHelper.readSpinnerOption(getActivity());
        if(CURRENT_OPTION != PreferencesHelper.FAVORITE_SELECTION){
            return;
        }
        setMoviesFromDb(data);
        setGridOfMovies();
        isFavorite = true;
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    // call it asynchronously
    public void setMoviesFromDb(Cursor cursor) {
        resultMovies = new ResultMovies();
        if (cursor.moveToFirst()) {
            ArrayList<Movie> list = new ArrayList<>();
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getLong(cursor.getColumnIndex(MoviesContract.MovieEntry._ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)));
                movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POPULARITY)));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)));
                movie.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                list.add(movie);
            } while (cursor.moveToNext());
            resultMovies.setResults(list);
        } else {
            return;
        }
    }

    /**
     * attaches the fetch results to the grid. NOTE: must be called asynchronously
     **/
    private void setGridOfMovies() {
        List<Movie> movies = resultMovies.getResults();
        if (movies.isEmpty()) {
            // TODO: display a message showing the user that the is no movies to be shown
            Log.d(DEBUG_TAG, "no movies found");
        }
        moviesAdapter = new MoviesAdapter(context, resultMovies.getResults());
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

    /**
     * executes a callback when it get the results from the network
     **/
    public void getResultMovies(int page) {
        apiConnection = new ApiConnection(context);
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
        // notify activity
        if (mListener != null) {
            mListener.onMovieClick(selectedMovie, isFavorite);
        }
    }

    @OnClick(R.id.prev_fab)
    void clickPreviousButton(View v) {
        paginate(v);
    }

    @OnClick(R.id.next_fab)
    void clickNextButton(View v) {
        paginate(v);
    }

    public void fetchMoviesFromNetwork() {
        getResultMovies(DEFAULT_PAGE);
    }

    public void fetchMoviesFromDb() {
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface callback {
        void onMovieClick(Movie movie, boolean isFavorite);
    }
}

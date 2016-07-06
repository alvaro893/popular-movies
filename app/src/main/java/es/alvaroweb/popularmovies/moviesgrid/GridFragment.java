package es.alvaroweb.popularmovies.moviesgrid;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.data.MoviesContract;
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
    private static final int DEFAULT_POSITION = 1;
    private static final String DEBUG_TAG = GridFragment.class.getSimpleName();
    private static final String SAVED_POSITION = "position";
    private static final String SAVED_PAGE = "page";
    @BindView(R.id.movies_grid_view) GridView moviesGridView;
    @BindView(R.id.next_fab) FloatingActionButton nextButton;
    @BindView(R.id.prev_fab) FloatingActionButton prevButton;


    private boolean mIsFavorite = false;
    private callback mListener;
    private Activity mActivity;
    private ResultMovies mResultMovies;
    private int mPosition = DEFAULT_POSITION;
    private int mPage = DEFAULT_PAGE;

    public GridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mPosition = savedInstanceState.getInt(SAVED_POSITION);
            mPage = savedInstanceState.getInt(SAVED_PAGE);
        }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PAGE, mPage);
        outState.putInt(SAVED_POSITION, mPosition);
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
                mActivity.setTitle(getString(R.string.favorites_title));
                return new CursorLoader(mActivity, uri, null, null, null, null);
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
        mIsFavorite = true;
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    // call it asynchronously
    public void setMoviesFromDb(Cursor cursor) {
        mResultMovies = new ResultMovies();
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
            mResultMovies.setResults(list);
        } else {
            return;
        }
    }

    /**
     * attaches the fetch results to the grid. NOTE: must be called asynchronously
     **/
    private void setGridOfMovies() {
        List<Movie> movies = mResultMovies.getResults();
        if (movies.isEmpty()) {
            Toast.makeText(getActivity(), R.string.no_movies_message, Toast.LENGTH_SHORT).show();
        }
        MoviesAdapter moviesAdapter = new MoviesAdapter(mActivity, mResultMovies.getResults());
        moviesGridView.setAdapter(moviesAdapter);
        nextButton.setVisibility(View.VISIBLE);
        prevButton.setVisibility(View.VISIBLE);
        // recover last position
        //moviesGridView.setSelection(mPosition);
        Log.d(DEBUG_TAG, "scroll to " +mPosition);
        moviesGridView.smoothScrollToPosition(mPosition);
    }

    private void paginate(View v) {
        int currentPage;
        if (v.getId() == R.id.prev_fab) {
            currentPage = mResultMovies.getPreviousPage();
        } else {
           currentPage = mResultMovies.getNextPage();
        }
        getResultMovies(currentPage);
        // save page
        mPage = currentPage;
        mPosition = DEFAULT_POSITION;
    }

    /**
     * executes a callback when it get the results from the network
     **/
    public void getResultMovies(int page) {
        ApiConnection apiConnection = new ApiConnection(mActivity);
        apiConnection.getMovies(page, new Callback<ResultMovies>() {
            @Override
            public void onResponse(Call<ResultMovies> call, Response<ResultMovies> response) {
                //Log.d(DEBUG_TAG, "response:" + response.code() + " for:" + call.request().url().toString());
                mResultMovies = response.body();
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
        Movie selectedMovie = mResultMovies.getResults().get(position);
        // notify activity
        if (mListener != null) {
            mListener.onMovieClick(selectedMovie, mIsFavorite);
        }
        //save position in the grid
        mPosition = position;
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
        getResultMovies(mPage);
    }

    public void fetchMoviesFromDb() {
        if(isAdded()) {
            getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        }
    }

    public void setDefaultState() {
        mPosition = DEFAULT_POSITION;
        mPage = DEFAULT_PAGE;
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

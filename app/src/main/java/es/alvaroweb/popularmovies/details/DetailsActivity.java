/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.details;

import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.data.MoviesContract;
import es.alvaroweb.popularmovies.helpers.LoadImageHelper;
import es.alvaroweb.popularmovies.helpers.MovieStoreHelper;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.model.ResultReviews;
import es.alvaroweb.popularmovies.model.ResultVideos;
import es.alvaroweb.popularmovies.networking.ApiConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Shows the details of a Movie after clicking it **/
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Movie movie;

    @BindView(R.id.plot_text_view)  TextView plot;
    @BindView(R.id.year_text_view) TextView year;
    @BindView(R.id.back_drop_image) ImageView backdrop;
    @BindView(R.id.title_text_view) TextView title;
    //@BindView(R.id.duration_text_view) TextView duration;
    @BindView(R.id.rating_text_view) TextView rating;
    @BindView(R.id.trailer_list_view) ListView trailerList;
    @BindView(R.id.review_list_view) ListView reviewList;
    @BindView(R.id.review_title_text_view) TextView reviewTitle;
    @BindView(R.id.favorite_fab) FloatingActionButton favoriteButton;
    private ResultVideos resultVideos;
    private VideoAdapter videoAdapter;
    private ReviewAdapter reviewAdapter;
    private ApiConnection apiConnection;
    private ResultReviews resultReviews;
    private static final int FAVORITE_LOADER = 0;
    private static final String DEBUG_TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ui tasks
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSelectedMovie();
        setTitle(movie.getTitle());
        initializeUiComponents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // network tasks
        apiConnection = new ApiConnection(this);
        requestResultVideos();
        requestResultReviews(1);

        // load data from db..
        getSupportLoaderManager().initLoader(FAVORITE_LOADER, null, this);


    }

    private void initializeUiComponents(){
        ButterKnife.bind(this);
        LoadImageHelper imageHelper = new LoadImageHelper(this);
        title.setText(movie.getTitle());
        plot.setText(movie.getOverview());
        year.setText(String.format("(%s)", movie.getReleaseDateAsArray()[0]));
        rating.setText(String.valueOf(movie.getVoteAverage()));

        //duration.setText(movie.getRuntime());
        imageHelper.setImage(backdrop, movie.getBackdropPath() , false);
    }

    private void movieIsFavorite() {
        favoriteButton.setImageResource(R.drawable.ic_star_yellow_24dp);
    }

    private void movieIsNotFavorite() {
        favoriteButton.setImageResource(R.drawable.ic_start_white_24dp);
    }


    private void requestResultVideos() {
        apiConnection.getVideos(movie.getId(), new TrailerCallback());
    }

    private void requestResultReviews(int page){
        apiConnection.getReviews(movie.getId(), page, new ReviewCallback());
    }

    /** get the selected movie by the user from intent **/
    private void getSelectedMovie(){
        Bundle extras = this.getIntent().getExtras();
        Serializable movie = extras.getSerializable(getString(R.string.SELECTED_MOVIE));
        this.movie = (Movie) movie;
    }

    /** must be called asynchronously **/
    private void setVideos(){
        videoAdapter = new VideoAdapter(this, resultVideos.getResults());
        trailerList.setAdapter(videoAdapter);
        justifyListViewHeightBasedOnChildren(trailerList);
    }

    /** must be called asynchronously **/
    private void setReviews(){
        reviewAdapter = new ReviewAdapter(this, resultReviews.getResults());
        reviewList.setAdapter(reviewAdapter);
        justifyListViewHeightBasedOnChildren(reviewList);
        if(reviewList.getCount() < 1){
            reviewTitle.setText(R.string.no_reviews_warning);
        }
    }

    @OnItemClick(R.id.trailer_list_view)
    void trailerClick(AdapterView<?> parent, View view, int position, long id){
        ResultVideos.Video trailer = resultVideos.getResults().get(position);
        Uri uri;
        switch (trailer.getSite()){
            case "YouTube":
                uri = Uri.parse("http://youtube.com/watch?v=" + trailer.getKey());
                break;
            default:
                throw new UnsupportedOperationException("no valid site");
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.favorite_fab)
    void favoriteButtonClick(View v){
        MovieStoreHelper storeHelper = new MovieStoreHelper(this);
        storeHelper.insertMovieorDeleteMovie(movie);
    }

    /** Calculates the height of a ListView in order to remove
     * the need of scrolling in that ListView
     */
    private void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    // Loader Callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        switch (loaderID){
            case FAVORITE_LOADER:{
                Uri uri = MoviesContract.MovieEntry.buildMovieUri(movie.getId());
                String[] projection = new String[]{MoviesContract.MovieEntry._ID};
                return new CursorLoader(this.getApplicationContext(),
                        uri,
                        projection,
                        null,
                        null,
                        null);
            }
            default:
                Log.e(DEBUG_TAG, "Invalid loader id was passed");
                return null;
        }

    }
    /** this callback updates the start button to indicate
     *  whether the movie is in database or not **/
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            movieIsFavorite();
        }else{
            movieIsNotFavorite();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Log.d(DEBUG_TAG, "onLoaderReset");
    }

    // End od Loader Callbacks


    private class ReviewCallback implements Callback<ResultReviews>{
        @Override
        public void onResponse(Call<ResultReviews> call, Response<ResultReviews> response) {
            resultReviews = response.body();
            setReviews();
        }

        @Override
        public void onFailure(Call<ResultReviews> call, Throwable t) {
            t.printStackTrace();
        }
    }

    private class TrailerCallback implements Callback<ResultVideos>{
        @Override
        public void onResponse(Call<ResultVideos> call, Response<ResultVideos> response) {
            resultVideos = response.body();
            setVideos();
        }

        @Override
        public void onFailure(Call<ResultVideos> call, Throwable t) {
            t.printStackTrace();
        }
    }

}

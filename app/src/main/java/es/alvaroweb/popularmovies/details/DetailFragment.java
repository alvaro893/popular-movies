package es.alvaroweb.popularmovies.details;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.zip.Inflater;

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

/**
 *
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String SELECTED_MOVIE_ARG = "selected movie";
    private static final int FAVORITE_LOADER = 0;
    private static final int REVIEWS_LOADER = 1;
    private static final int VIDEOS_LOADER = 2;
    private static final int DEFAULT_PAGE = 1;
    private static final String DEBUG_TAG = DetailFragment.class.getSimpleName();
    public static final String IS_FAVORITE_ARG = "is favorite";
    @BindView(R.id.plot_text_view) TextView plot;
    @BindView(R.id.year_text_view) TextView year;
    @BindView(R.id.back_drop_image)ImageView backdrop;
    @BindView(R.id.title_text_view) TextView title;
    @BindView(R.id.rating_text_view) TextView rating;
    @BindView(R.id.review_title_text_view) TextView reviewTitle;
    @BindView(R.id.favorite_fab) FloatingActionButton favoriteButton;
    @BindView(R.id.review_linear_layout) LinearLayout reviewContainer;
    @BindView(R.id.videos_linear_layout) LinearLayout videosContainer;
    Movie movie;
    private ResultVideos resultVideos;
    private ApiConnection apiConnection;
    private ResultReviews resultReviews;
    private InsertOrDeleteMovieTask dbTask;
    private FragmentActivity activity;
    private boolean isFavorite = false;
    private View viewRoot;


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getFragmentArguments();
        networkAndDatabaseTasks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewRoot =  inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, viewRoot);
        LoadImageHelper imageHelper = new LoadImageHelper(activity);
        title.setText(movie.getTitle());
        plot.setText(movie.getOverview());
        year.setText(String.format("(%s)", movie.getReleaseDateAsArray()[0]));
        rating.setText(String.valueOf(movie.getVoteAverage()));

        imageHelper.setImage(backdrop, movie.getBackdropPath() , false);
        return viewRoot;
    }

    @Override
    public void onStop() {
        super.onStop();
        // avoid memory leaks. TODO: this is not enough to stop a memory leak.
        if(dbTask != null){
            dbTask.cancel(true);
        }
    }

    private void networkAndDatabaseTasks() {
        if(isFavorite){
            getLoaderManager().initLoader(REVIEWS_LOADER,null,this);
            getLoaderManager().initLoader(VIDEOS_LOADER,null,this);
        }else{
            apiConnection = new ApiConnection(activity);
            requestResultVideos();
            requestResultReviews(DEFAULT_PAGE);
        }

        // this loader will check if the movie is a favorite
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
    }
    /** creates a review and adds it the review section **/
    private void addReview(ResultReviews.Review review){
        LinearLayout reviewView = (LinearLayout) LayoutInflater
                .from(activity)
                .inflate(R.layout.review_row_layout, reviewContainer, false);
        TextView auth = (TextView) reviewView.findViewById(R.id.review_author_text_view);
        TextView content = (TextView) reviewView.findViewById(R.id.review_content_text_view);

        auth.setText(review.getAuthor());
        content.setText((review.getContent()));

        reviewContainer.addView(reviewView);
    }

    private void addVideo(ResultVideos.Video video){
        LinearLayout videoView = (LinearLayout) LayoutInflater
                .from(activity)
                .inflate(R.layout.trailer_row_layout, videosContainer, false);

        TextView name = (TextView) videoView.findViewById(R.id.trailer_text_view);
        TextView site = (TextView) videoView.findViewById(R.id.source_trailer_text_view);

        name.setText(video.getName());
        site.setText(video.getSite());

        int index = resultVideos.getResults().indexOf(video);
        videoView.setTag(index);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                trailerClick(index);
            }
        });

        videosContainer.addView(videoView);
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

    /** get the selected movie by the user from arguments **/
    private void getFragmentArguments(){
        Bundle extras = getArguments();
        Serializable movie = extras.getSerializable(SELECTED_MOVIE_ARG);
        this.isFavorite = extras.getBoolean(IS_FAVORITE_ARG);
        this.movie = (Movie) movie;
    }

    /** must be called asynchronously **/
    private void setVideos(){
        for(ResultVideos.Video v: resultVideos.getResults()){
            addVideo(v);
        }
    }

    /** must be called asynchronously **/
    private void setReviews(){
        Log.d(DEBUG_TAG, "reviews:"+resultReviews.getResults().size());
        for(ResultReviews.Review r: resultReviews.getResults()){
            addReview(r);
        }

        if(resultReviews.getResults().size() <= 0){
            reviewTitle.setText(R.string.no_reviews_warning);
        }
    }

    void trailerClick(int position){
        ResultVideos.Video trailer = resultVideos.getResults().get(position);
        Uri uri;
        switch (trailer.getSite()){
            case "YouTube":
                uri = Uri.parse("http://youtube.com/watch?v=" + trailer.getKey());
                break;
            default:
                throw new UnsupportedOperationException("no valid site " + trailer.getSite());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.favorite_fab)
    void favoriteButtonClick(View v){
        dbTask = new InsertOrDeleteMovieTask();
        dbTask.execute();
    }


    // Loader Callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        switch (loaderID){
            //this is loader is to get visual feedback whether the movie is favorite
            case FAVORITE_LOADER:{
                Uri uri = MoviesContract.MovieEntry.buildMovieUri(movie.getId());
                String[] projection = new String[]{MoviesContract.MovieEntry._ID};
                return new CursorLoader(activity,
                        uri,
                        projection,
                        null,
                        null,
                        null);
            }
            case REVIEWS_LOADER:{
                Uri uri = MoviesContract.ReviewEntry.buildReviewUri(movie.getId());
                return new CursorLoader(activity,
                        uri,
                        null,null,null,null);
            }
            case VIDEOS_LOADER:{
                Uri uri = MoviesContract.VideoEntry.buildVideoUri(movie.getId());
                return  new CursorLoader(activity,
                        uri,
                        null, null, null, null);
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
        switch (loader.getId()){
            case FAVORITE_LOADER:
                if(data.moveToFirst()){
                    movieIsFavorite();
                }else{
                    movieIsNotFavorite();
                }
                break;
            case REVIEWS_LOADER:
                setReviewsFromDb(data);
                setReviews();
                break;
            case VIDEOS_LOADER:
                setVideosFromDb(data);
                setVideos();
                break;
        }
    }

    private void setVideosFromDb(Cursor data) {
        resultVideos = new ResultVideos();
        if(data.moveToFirst()){
            do{
                ResultVideos.Video video = new ResultVideos.Video();
                video.setName(data.getString(data.getColumnIndex(MoviesContract.VideoEntry.COLUMN_NAME)));
                video.setSite(data.getString(data.getColumnIndex(MoviesContract.VideoEntry.COLUMN_SITE)));
                video.setKey(data.getString(data.getColumnIndex(MoviesContract.VideoEntry.COLUMN_KEY)));
                resultVideos.getResults().add(video);
            }while (data.moveToNext());

        }
    }

    private void setReviewsFromDb(Cursor data) {
        resultReviews = new ResultReviews();
        if(data.moveToFirst()){
            do{
                ResultReviews.Review review = new ResultReviews.Review();
                review.setAuthor(data.getString(data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR)));
                review.setContent(data.getString(data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT)));
                resultReviews.getResults().add(review);
            }while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Log.d(DEBUG_TAG, "onLoaderReset");
    }

    // End od Loader Callbacks


    private class ReviewCallback implements Callback<ResultReviews> {
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

    /** It perfoms the task of insert a movie or delete asynchronously, since Cursor loader
     * are only to do query operations**/
    private class InsertOrDeleteMovieTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MovieStoreHelper storeHelper = new MovieStoreHelper(getActivity(),
                    movie, resultReviews, resultVideos);
            storeHelper.insertMovieorDeleteMovie();
            return null;
        }
    }
}

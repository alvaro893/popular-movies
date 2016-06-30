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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    public static final String SELECTED_MOVIE_ARG = "selected mMovie";
    private static final int FAVORITE_LOADER = 0;
    private static final int REVIEWS_LOADER = 1;
    private static final int VIDEOS_LOADER = 2;
    private static final int DEFAULT_PAGE = 1;
    private static final String DEBUG_TAG = DetailFragment.class.getSimpleName();
    public static final String IS_FAVORITE_ARG = "is favorite";
    @BindView(R.id.plot_text_view) TextView mPlot;
    @BindView(R.id.year_text_view) TextView mYear;
    @BindView(R.id.back_drop_image)ImageView mBackdrop;
    @BindView(R.id.title_text_view) TextView mTitle;
    @BindView(R.id.rating_text_view) TextView mRating;
    @BindView(R.id.review_title_text_view) TextView mReviewTitle;
    @BindView(R.id.favorite_fab) FloatingActionButton mFavoriteButton;
    @BindView(R.id.review_linear_layout) LinearLayout mReviewContainer;
    @BindView(R.id.videos_linear_layout) LinearLayout mVideosContainer;
    Movie mMovie;
    private ResultVideos mResultVideos;
    private ApiConnection mApiConnection;
    private ResultReviews mResultReviews;
    private InsertOrDeleteMovieTask mDbTask;
    private FragmentActivity mActivity;
    private boolean mIsFavorite = false;


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        getFragmentArguments();
        networkAndDatabaseTasks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, viewRoot);
        LoadImageHelper imageHelper = new LoadImageHelper(mActivity);
        mTitle.setText(mMovie.getTitle());
        mPlot.setText(mMovie.getOverview());
        mYear.setText(String.format("(%s)", mMovie.getReleaseDateAsArray()[0]));
        mRating.setText(String.valueOf(mMovie.getVoteAverage()));

        imageHelper.setImage(mBackdrop, mMovie.getBackdropPath() , false);
        return viewRoot;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mDbTask != null){
            mDbTask.cancel(true);
        }
    }

    private void networkAndDatabaseTasks() {
        if(mIsFavorite){
            getLoaderManager().initLoader(REVIEWS_LOADER,null,this);
            getLoaderManager().initLoader(VIDEOS_LOADER,null,this);
        }else{
            mApiConnection = new ApiConnection(mActivity);
            requestResultVideos();
            requestResultReviews(DEFAULT_PAGE);
        }

        // this loader will check if the mMovie is a favorite
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
    }
    /** creates a review and adds it the review section **/
    private void addReview(ResultReviews.Review review){
        LinearLayout reviewView = (LinearLayout) LayoutInflater
                .from(mActivity)
                .inflate(R.layout.review_row_layout, mReviewContainer, false);
        TextView auth = (TextView) reviewView.findViewById(R.id.review_author_text_view);
        TextView content = (TextView) reviewView.findViewById(R.id.review_content_text_view);

        auth.setText(review.getAuthor());
        content.setText((review.getContent()));

        mReviewContainer.addView(reviewView);
    }

    private void addVideo(ResultVideos.Video video){
        LinearLayout videoView = (LinearLayout) LayoutInflater
                .from(mActivity)
                .inflate(R.layout.trailer_row_layout, mVideosContainer, false);

        TextView name = (TextView) videoView.findViewById(R.id.trailer_text_view);
        TextView site = (TextView) videoView.findViewById(R.id.source_trailer_text_view);

        name.setText(video.getName());
        site.setText(video.getSite());

        int index = mResultVideos.getResults().indexOf(video);
        videoView.setTag(index);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                trailerClick(index);
            }
        });

        mVideosContainer.addView(videoView);
    }


    private void movieIsFavorite() {
        mFavoriteButton.setImageResource(R.drawable.ic_star_yellow_24dp);
    }

    private void movieIsNotFavorite() {
        mFavoriteButton.setImageResource(R.drawable.ic_start_white_24dp);
    }


    private void requestResultVideos() {
        mApiConnection.getVideos(mMovie.getId(), new TrailerCallback());
    }

    private void requestResultReviews(int page){
        mApiConnection.getReviews(mMovie.getId(), page, new ReviewCallback());
    }


    private void showFavoriteButton() {
        mFavoriteButton.setVisibility(View.VISIBLE);
    }

    /** get the selected mMovie by the user from arguments **/
    private void getFragmentArguments(){
        Bundle extras = getArguments();
        Serializable movie = extras.getSerializable(SELECTED_MOVIE_ARG);
        this.mIsFavorite = extras.getBoolean(IS_FAVORITE_ARG);
        this.mMovie = (Movie) movie;
    }

    /** must be called asynchronously **/
    private void setVideos(){
        for(ResultVideos.Video v: mResultVideos.getResults()){
            addVideo(v);
        }
    }

    /** must be called asynchronously **/
    private void setReviews(){
        Log.d(DEBUG_TAG, "reviews:"+ mResultReviews.getResults().size());
        for(ResultReviews.Review r: mResultReviews.getResults()){
            addReview(r);
        }

        if(mResultReviews.getResults().size() <= 0){
            mReviewTitle.setText(R.string.no_reviews_warning);
        }
    }

    void trailerClick(int position){
        ResultVideos.Video trailer = mResultVideos.getResults().get(position);
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
        mDbTask = new InsertOrDeleteMovieTask();
        mDbTask.execute();
    }


    // Loader Callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        switch (loaderID){
            //this is loader is to get visual feedback whether the mMovie is favorite
            case FAVORITE_LOADER:{
                Uri uri = MoviesContract.MovieEntry.buildMovieUri(mMovie.getId());
                String[] projection = new String[]{MoviesContract.MovieEntry._ID};
                return new CursorLoader(mActivity,
                        uri,
                        projection,
                        null,
                        null,
                        null);
            }
            case REVIEWS_LOADER:{
                Uri uri = MoviesContract.ReviewEntry.buildReviewUri(mMovie.getId());
                return new CursorLoader(mActivity,
                        uri,
                        null,null,null,null);
            }
            case VIDEOS_LOADER:{
                Uri uri = MoviesContract.VideoEntry.buildVideoUri(mMovie.getId());
                return  new CursorLoader(mActivity,
                        uri,
                        null, null, null, null);
            }
            default:
                Log.e(DEBUG_TAG, "Invalid loader id was passed");
                return null;
        }

    }
    /** this callback updates the start button to indicate
     *  whether the mMovie is in database or not **/
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
                showFavoriteButton();
                break;
            case VIDEOS_LOADER:
                setVideosFromDb(data);
                setVideos();
                break;
        }
    }


    private void setVideosFromDb(Cursor data) {
        mResultVideos = new ResultVideos();
        if(data.moveToFirst()){
            do{
                ResultVideos.Video video = new ResultVideos.Video();
                video.setName(data.getString(data.getColumnIndex(MoviesContract.VideoEntry.COLUMN_NAME)));
                video.setSite(data.getString(data.getColumnIndex(MoviesContract.VideoEntry.COLUMN_SITE)));
                video.setKey(data.getString(data.getColumnIndex(MoviesContract.VideoEntry.COLUMN_KEY)));
                mResultVideos.getResults().add(video);
            }while (data.moveToNext());

        }
    }

    private void setReviewsFromDb(Cursor data) {
        mResultReviews = new ResultReviews();
        if(data.moveToFirst()){
            do{
                ResultReviews.Review review = new ResultReviews.Review();
                review.setAuthor(data.getString(data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR)));
                review.setContent(data.getString(data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT)));
                mResultReviews.getResults().add(review);
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
            mResultReviews = response.body();
            setReviews();
            showFavoriteButton();
        }

        @Override
        public void onFailure(Call<ResultReviews> call, Throwable t) {
            t.printStackTrace();
        }
    }

    private class TrailerCallback implements Callback<ResultVideos>{
        @Override
        public void onResponse(Call<ResultVideos> call, Response<ResultVideos> response) {
            mResultVideos = response.body();
            setVideos();
        }

        @Override
        public void onFailure(Call<ResultVideos> call, Throwable t) {
            t.printStackTrace();
        }
    }

    /** It perfoms the task of insert a mMovie or delete asynchronously, since Cursor loader
     * are only to do query operations**/
    private class InsertOrDeleteMovieTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MovieStoreHelper storeHelper = new MovieStoreHelper(getActivity(),
                    mMovie, mResultReviews, mResultVideos);
            storeHelper.insertMovieorDeleteMovie();
            return null;
        }
    }
}

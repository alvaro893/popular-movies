/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.details;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.helpers.LoadImageHelper;
import es.alvaroweb.popularmovies.model.Movie;
import es.alvaroweb.popularmovies.model.ResultReviews;
import es.alvaroweb.popularmovies.model.ResultVideos;
import es.alvaroweb.popularmovies.networking.ApiConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Shows the details of a Movie after clicking it **/
public class DetailsActivity extends AppCompatActivity {
    private Movie movie;

    @BindView(R.id.plot_text_view)  TextView plot;
    @BindView(R.id.year_text_view) TextView year;
    @BindView(R.id.back_drop_image) ImageView backdrop;
    @BindView(R.id.title_text_view) TextView title;
    @BindView(R.id.rating_bar) RatingBar rating;
    @BindView(R.id.trailer_list_view) ListView trailerView;
    @BindView(R.id.review_list_view) ListView reviewView;
    private ResultVideos resultVideos;
    private VideoAdapter videoAdapter;
    private ReviewAdapter reviewAdapter;
    private ApiConnection apiConnection;
    private ResultReviews resultReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiConnection = new ApiConnection(this);

        ButterKnife.bind(this);
        getSelectedMovie();
        setTitle(movie.getTitle());
        getResultVideos();
        getResultReviews(1);

        initializeUiComponents();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeUiComponents(){
        LoadImageHelper imageHelper = new LoadImageHelper(this);

        title.setText(movie.getTitle());
        plot.setText(movie.getOverview());
        year.setText(String.format("(%s)", movie.getReleaseDate()[0]));
        rating.setRating( movie.getVoteAverage() / 2);

        //fix the rating bar color (for previous android versions)
        Drawable drawable = rating.getProgressDrawable();
        drawable.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        imageHelper.setImage(backdrop, movie.getBackdropPath() , false);

    }

    private void getResultVideos() {
        apiConnection.getVideos(movie.getId(), new Callback<ResultVideos>() {
            @Override
            public void onResponse(Call<ResultVideos> call, Response<ResultVideos> response) {
                resultVideos = response.body();
                setVideoAdapter();
            }

            @Override
            public void onFailure(Call<ResultVideos> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getResultReviews(int page){
        Callback<ResultReviews> callback = new Callback<ResultReviews>() {
            @Override
            public void onResponse(Call<ResultReviews> call, Response<ResultReviews> response) {
                resultReviews = response.body();
                setReviewAdapter();
            }

            @Override
            public void onFailure(Call<ResultReviews> call, Throwable t) {
                t.printStackTrace();
            }
        };
        apiConnection.getReviews(movie.getId(), page, callback);
    }

    private void getSelectedMovie(){
        Bundle extras = this.getIntent().getExtras();
        Serializable movie = extras.getSerializable(getString(R.string.SELECTED_MOVIE));
        this.movie = (Movie) movie;

    }

    private void setVideoAdapter(){
        videoAdapter = new VideoAdapter(this, resultVideos.getResults());
        trailerView.setAdapter(videoAdapter);
        justifyListViewHeightBasedOnChildren(trailerView);
    }

    private void setReviewAdapter(){
        reviewAdapter = new ReviewAdapter(this, resultReviews.getResults());
        reviewView.setAdapter(reviewAdapter);
        justifyListViewHeightBasedOnChildren(reviewView);
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

    public void justifyListViewHeightBasedOnChildren (ListView listView) {

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

}

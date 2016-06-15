/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.details;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.helpers.LoadImageHelper;
import es.alvaroweb.popularmovies.model.Movie;

/** Shows the details of a Movie after clicking it **/
public class DetailsActivity extends AppCompatActivity {
    private Movie movie;
    @BindView(R.id.plot_text_view)  TextView plot;
    @BindView(R.id.year_text_view) TextView year;
    @BindView(R.id.back_drop_image) ImageView backdrop;
    @BindView(R.id.title_text_view) TextView title;
    @BindView(R.id.rating_bar) RatingBar rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        getSelectedMovie();
        setTitle(movie.getTitle());

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

    private void getSelectedMovie(){
        Bundle extras = this.getIntent().getExtras();
        Serializable movie = extras.getSerializable(getString(R.string.SELECTED_MOVIE));
        this.movie = (Movie) movie;

    }

}

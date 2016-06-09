package es.alvaroweb.popularmovies;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Rating;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;

public class DetailsActivity extends AppCompatActivity {
    private Movie movie;
    private TextView plot;
    private TextView year;
    private ImageView backdrop;
    private TextView title;
    private RatingBar rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSelectedMovie();
        setTitle(movie.getTitle());

        initializeUiComponents();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeUiComponents(){
        LoadImageHelper imageHelper = new LoadImageHelper(this);
        title = (TextView) findViewById(R.id.title_text_view);
        plot = (TextView) findViewById(R.id.plot_text_view);
        year = (TextView) findViewById(R.id.year_text_view);
        backdrop = (ImageView) findViewById(R.id.back_drop_image);
        rating = (RatingBar) findViewById(R.id.rating_bar);

        title.setText(movie.getTitle());
        plot.setText(movie.getOverview());
        year.setText("("+movie.getReleaseDate() [0] + ")");
        rating.setRating( movie.getVoteAverage() / 2);

        //fix the rating bar color
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

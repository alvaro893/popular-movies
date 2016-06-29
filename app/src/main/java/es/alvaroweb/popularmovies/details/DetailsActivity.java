/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.details;

import android.os.AsyncTask;
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
import java.util.ArrayList;

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
public class DetailsActivity extends AppCompatActivity  {

    private static final String DEBUG_TAG = DetailsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ui tasks
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            addFragmentDetail();
        }

    }

    private void addFragmentDetail() {
        Bundle args = getIntent().getExtras();

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}

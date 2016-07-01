/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.details;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import es.alvaroweb.popularmovies.R;

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

        setTitle(getString(R.string.activity_details_title));

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
                .commit();
    }

}

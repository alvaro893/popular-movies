/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.moviesgrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import es.alvaroweb.popularmovies.R;
import es.alvaroweb.popularmovies.helpers.LoadImageHelper;
import es.alvaroweb.popularmovies.model.Movie;

/*
 *  Adapter for Movies in the Main Activity
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {
    private static final String DEBUG_TAG = MoviesAdapter.class.getSimpleName();
    private ImageView image;
    private LoadImageHelper imageHelper;

    public MoviesAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
        imageHelper = new LoadImageHelper(getContext());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        //Log.d(DEBUG_TAG, movie.getTitle());

        // Inflate layout if it is not being reused
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movie_row_layout, parent, false);
        }

        image = (ImageView) convertView.findViewById(R.id.movie_image_row);
        imageHelper.setImage(image, movie.getPosterPath(), true);
        return convertView;
    }
}

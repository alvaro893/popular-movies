/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

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

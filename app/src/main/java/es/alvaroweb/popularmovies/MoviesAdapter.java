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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/*
 *  Adapter for Movies in the Main Activity
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    private static final String DEBUG_TAG = MoviesAdapter.class.getSimpleName();
    private TextView title;
    private ImageView image;

    public MoviesAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movie_row_layout, parent, false);
            title = (TextView) convertView.findViewById(R.id.movie_title_row);
            image = (ImageView) convertView.findViewById(R.id.movie_image_row);

            title.setText(movie.getTitle());
            setImage(image, movie.getPosterPath());

        }
        return convertView;
    }
    /** Loads and sets the image in the given relativePath parameter using a external library */
    private void setImage(ImageView view, String relativePath) {
        String urlToLoad = IMAGE_BASE_URL + IMAGE_SIZE + relativePath;
        Glide
            .with(getContext())
            .load(urlToLoad)
            .centerCrop()
            .error(android.R.drawable.ic_delete) // TODO: look for better image error
            .into(view);
    }
}

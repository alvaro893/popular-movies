/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import es.alvaroweb.popularmovies.R;

/*
 * Manages the use of Glide (Image Loading library)
 */
public class LoadImageHelper {
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w342";
    private final Context mContext;

    // private static final String[] SIZES = { "w92", "w154", "w185", "w342", "w500", "w780", "original"};
    public LoadImageHelper(Context context){
        this.mContext = context;
    }

    /** Loads and sets the image in the given relativePath parameter using a external library */
    public void setImage(ImageView view, String relativePath, boolean cropcenter) {
        String urlToLoad = IMAGE_BASE_URL + IMAGE_SIZE + relativePath;
        DrawableRequestBuilder<String> stringDrawableRequestBuilder = Glide
                .with(mContext)
                .load(urlToLoad)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.unload_image_24dp)
                .fitCenter();
        if (cropcenter){
            stringDrawableRequestBuilder
                    .centerCrop()
                    .into(view);
        }
        stringDrawableRequestBuilder.into(view);
    }

}

/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.moviesgrid;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 *  It customizes the Spinner in MainActivity
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    public SpinnerAdapter(Context context, int resource, String[] strArray) {
        super(context, resource, strArray);
    }

    /** this is basically to delete the spinner title*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText("");
        return view;
    }

}

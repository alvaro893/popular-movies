/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import es.alvaroweb.popularmovies.R;

/*
 * handles shared preferences through the application
 */
public class PreferencesHelper {

    public static final int TOP_SELECTION = 0;
    public static final int POPULAR_SELECTION = 1;
    public static final int FAVORITE_SELECTION = 2;

    public static void setSpinnerOption(int option, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.pref_spinner_option), option);
        editor.apply();
    }

    public static int readSpinnerOption(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        int defaultValue = POPULAR_SELECTION;
        int option = sharedPref.getInt(context.getString(R.string.pref_spinner_option), defaultValue);
        return option;
    }
}

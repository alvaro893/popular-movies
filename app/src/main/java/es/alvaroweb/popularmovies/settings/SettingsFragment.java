/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */

package es.alvaroweb.popularmovies.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import es.alvaroweb.popularmovies.R;

/*
 *  Loads the preferences
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}

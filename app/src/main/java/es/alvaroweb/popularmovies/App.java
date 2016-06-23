/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/*
 * TODO: Create JavaDoc
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        final Context context=this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}

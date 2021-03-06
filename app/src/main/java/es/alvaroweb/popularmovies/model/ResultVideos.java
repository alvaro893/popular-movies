/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.model;

import java.util.ArrayList;
import java.util.List;

/*
 * represents the videos (trailers) from JSON retrieved from server
 */
public class ResultVideos {
    long id;
    List<Video> results;

    public ResultVideos() {
        results = new ArrayList<>();
    }

    public static class Video {
        String key;
        String name;
        String site;

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }


    public void setResults(List<Video> results) {
        this.results = results;
    }
}

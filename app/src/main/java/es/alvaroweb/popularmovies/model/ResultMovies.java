/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Represents a bundle of Movies, objects from this class are deserialized from
 * a JSON document.
 */
public class ResultMovies {
    int page;
    List<Movie> results;
    @SerializedName("total_results")
    int totalResults;
    @SerializedName("total_pages")
    int totalPages;

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}

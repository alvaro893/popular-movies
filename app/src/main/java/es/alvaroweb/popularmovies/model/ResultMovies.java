/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

    public ResultMovies() {
        results = new ArrayList<>();
    }

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

    public void setPage(int page) {
        this.page = page;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNextPage(){
        int next = page + 1;
        if (next <= totalPages){
            return next;
        }else return 0;
    }

    public int getPreviousPage(){
        int prev = page - 1;
        if (prev > 0){
            return prev;
        }else return 0;
    }
}

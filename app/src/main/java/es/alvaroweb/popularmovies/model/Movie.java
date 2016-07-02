/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package es.alvaroweb.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
/*
 * Represent a movie, objects from this class are deserialized from
 * a JSON document. Note: it does not use all properties in the JSON but
 * can be extended
 */
public class Movie implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new ParcelableCreatorMovie();
    private String title;
    private long id;
    private double popularity;
    private String overview;
    @SerializedName("vote_average")
    private float voteAverage;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    public Movie(){

    }

    protected Movie(Parcel in) {
        title = in.readString();
        id = in.readLong();
        popularity = in.readDouble();
        overview = in.readString();
        voteAverage = in.readFloat();
        releaseDate = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    /** Returns an array of strings with the date:
     * [0] -> year,
     * [1] -> month,
     * [2] -> day
     * */
    public String[] getReleaseDateAsArray() {
        String[] date = releaseDate.split("-");
        return date;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return String.format("%s, %d", title, id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(id);
        dest.writeDouble(popularity);
        dest.writeString(overview);
        dest.writeFloat(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
    }

    private static class ParcelableCreatorMovie implements Parcelable.Creator<Movie>{
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    }
}
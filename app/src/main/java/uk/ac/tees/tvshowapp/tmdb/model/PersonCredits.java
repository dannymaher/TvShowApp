package uk.ac.tees.tvshowapp.tmdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonCredits<T> {
    @SerializedName("cast")
    @Expose
    private List<T> cast;

    @SerializedName("crew")
    @Expose
    private List<T> crew;

    public List<T> getCast() {
        return cast;
    }

    public List<T> getCrew() {
        return crew;
    }
}

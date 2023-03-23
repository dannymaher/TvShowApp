package uk.ac.tees.tvshowapp.tmdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class SpokenLanguageFilm implements Serializable {

    @SerializedName("iso_639_1")
    @Expose
    private String initial;
    @SerializedName("name")
    @Expose
    private String name;

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpokenLanguageFilm that = (SpokenLanguageFilm) o;
        return Objects.equals(initial, that.initial) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initial, name);
    }
}
package uk.ac.tees.tvshowapp.tmdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class ProductionCountry implements Serializable {
    @SerializedName("iso_3166_1")
    @Expose
    private String countryId;
    @SerializedName("name")
    @Expose
    private String name;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
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
        ProductionCountry that = (ProductionCountry) o;
        return Objects.equals(countryId, that.countryId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId, name);
    }
}

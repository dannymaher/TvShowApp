package uk.ac.tees.tvshowapp.tmdb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.ac.tees.tvshowapp.tmdb.model.CastImage;

public class PersonImages {

    @SerializedName("profiles")
    @Expose
    private List<CastImage> profiles;

    public List<CastImage> getProfiles() {
        return profiles;
    }
}

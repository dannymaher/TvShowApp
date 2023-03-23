package uk.ac.tees.tvshowapp.tmdb.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.tmdb.Configuration;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.listeners.PersonUpdateListener;
import uk.ac.tees.tvshowapp.tmdb.model.enums.ProfileImageSize;

public class Person implements Serializable {

    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("known_for_department")
    @Expose
    private String knownForDepartment;
    @SerializedName("deathday")
    @Expose
    private String deathday;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("also_known_as")
    @Expose
    private List<String> alsoKnownAs = null;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("biography")
    @Expose
    private String biography;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("place_of_birth")
    @Expose
    private String placeOfBirth;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;
    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("homepage")
    @Expose
    private String homepage;

    public String getBirthday() {

        if (birthday != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                // output date in correct format bases on user locale
                Date date = dateFormat.parse( birthday);
                return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
            } catch (ParseException e) {
                return birthday;
            }
        }
        return null;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getKnownForDepartment() {
        return knownForDepartment;
    }

    public void setKnownForDepartment(String knownForDepartment) {
        this.knownForDepartment = knownForDepartment;
    }

    public String getDeathday() {
        if (deathday != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                // output date in correct format bases on user locale
                Date date = dateFormat.parse(deathday);
                return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
            } catch (ParseException e) {
                return deathday;
            }
        }
        return null;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAlsoKnownAs() {
        return alsoKnownAs;
    }

    public void setAlsoKnownAs(List<String> alsoKnownAs) {
        this.alsoKnownAs = alsoKnownAs;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getProfile(ProfileImageSize imageSize) {
        if (profilePath != null) {
            return Configuration.IMAGE_URL + imageSize.name() + profilePath;
        }
        return null;
    }

    public String getProfilePath(){
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public void ensureComplete(final PersonUpdateListener listener){
        Call<Person> personCall = TmdbApi.get().getPersonService().getPerson(getId());
        personCall.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if(response.isSuccessful()){
                    updateInfo(response.body());
                    listener.onPersonUpdate();
                }
                else {
                    listener.onPersonFailure();
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {

            }
        });
    }
    private void updateInfo(Person update){

        this.name = update.name;
        this.deathday = update.deathday;
        this.gender = update.gender;
        this.birthday = update.birthday;
        this.homepage = update.homepage;
        this.biography = update.biography;
        this.placeOfBirth = update.placeOfBirth;
        this.homepage = update.homepage;
    }

}
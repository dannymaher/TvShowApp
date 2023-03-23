package uk.ac.tees.tvshowapp.tmdb.model;

import android.content.Context;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import uk.ac.tees.tvshowapp.MediaRepository;
import uk.ac.tees.tvshowapp.tmdb.Configuration;
import uk.ac.tees.tvshowapp.tmdb.listeners.TVShowUpdateListener;
import uk.ac.tees.tvshowapp.tmdb.model.enums.BackdropImageSize;
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

public class TVShow implements Serializable {

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("episode_run_time")
    @Expose
    private List<Integer> episodeRunTime = null;
    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("in_production")
    @Expose
    private Boolean inProduction;
    @SerializedName("languages")
    @Expose
    private List<String> languages = null;
    @SerializedName("last_air_date")
    @Expose
    private String lastAirDate;
    @SerializedName("last_episode_to_air")
    @Expose
    private Episode lastEpisodeToAir;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("next_episode_to_air")
    @Expose
    private Episode nextEpisodeToAir;
    @SerializedName("number_of_episodes")
    @Expose
    private Integer numberOfEpisodes;
    @SerializedName("number_of_seasons")
    @Expose
    private Integer numberOfSeasons;
    @SerializedName("origin_country")
    @Expose
    private List<String> originCountry = null;
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_name")
    @Expose
    private String originalName;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("seasons")
    @Expose
    private List<Season> seasons = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getBackdrop(BackdropImageSize size) {
        return Configuration.IMAGE_URL + size.toString() + backdropPath;
    }

    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getInProduction() {
        return inProduction;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public Episode getLastEpisodeToAir() {
        return lastEpisodeToAir;
    }

    public String getName() {
        return name;
    }

    public Episode getNextEpisodeToAir() {
        return nextEpisodeToAir;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public List<String> getOriginCountry() {
        return originCountry;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getOverview() {
        return overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getPoster(PosterImageSize size) {
        if(posterPath != null){
            return Configuration.IMAGE_URL + size.toString() + posterPath;
        }
        return null;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public String getDateString() {
        if(firstAirDate == null || lastAirDate == null){
            return "No date information.";
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

            Date firstAirDate = format.parse(getFirstAirDate());
            String firstAirYear = yearFormat.format(firstAirDate);

            String lastAirYear;
            if(getInProduction()){
                 lastAirYear= "";
            }else{
                Date lastAirDate = format.parse(getLastAirDate());
                lastAirYear = yearFormat.format(lastAirDate);
            }

            return "(" + firstAirYear + " - " + lastAirYear + ")";

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "No date information.";
    }

    // make sure all details are available
    public void ensureComplete(final TVShowUpdateListener updateListener, Context context, CompositeDisposable compositeDisposable){
        // check if already complete
        if(getInProduction() != null){
            updateListener.onTVShowUpdate();
            return;
        }

        // get the show and and call updateInfo when done
        Disposable disposable = MediaRepository.get(context).getTVShow(id).subscribe(tvShow -> {
            updateInfo(tvShow);
            updateListener.onTVShowUpdate();
        }, throwable -> {
            updateListener.onFailure();
        });
        compositeDisposable.add(disposable);

    }

    // copy missing information from returned TVShow object into this
    private void updateInfo(TVShow updated){
        inProduction = updated.getInProduction();
        firstAirDate = updated.getFirstAirDate();
        lastAirDate = updated.getLastAirDate();

        seasons = updated.getSeasons();
        genres = updated.getGenres();

        lastEpisodeToAir = updated.getLastEpisodeToAir();
        nextEpisodeToAir = updated.getNextEpisodeToAir();
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setEpisodeRunTime(List<Integer> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInProduction(Boolean inProduction) {
        this.inProduction = inProduction;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public void setLastEpisodeToAir(Episode lastEpisodeToAir) {
        this.lastEpisodeToAir = lastEpisodeToAir;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNextEpisodeToAir(Episode nextEpisodeToAir) {
        this.nextEpisodeToAir = nextEpisodeToAir;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public void setOriginCountry(List<String> originCountry) {
        this.originCountry = originCountry;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TVShow tvShow = (TVShow) o;
        return Objects.equals(id, tvShow.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
package uk.ac.tees.tvshowapp.tmdb.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uk.ac.tees.tvshowapp.tmdb.Configuration;
import uk.ac.tees.tvshowapp.tmdb.model.enums.EpisodeImageSize;

public class Episode implements Serializable {

    @SerializedName("air_date")
    @Expose
    private String airDate;
    @SerializedName("crew")
    @Expose
    private List<Person> crew = null;
    @SerializedName("episode_number")
    @Expose
    private Integer episodeNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("production_code")
    @Expose
    private String productionCode;
    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;
    @SerializedName("still_path")
    @Expose
    private String stillPath;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    public String getAirDateFormatted() {
        if (airDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                // output date in correct format bases on user locale
                Date date = dateFormat.parse(airDate);
                return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
            } catch (ParseException e) {
                return airDate;
            }
        }
        return null;
    }

    public String getAirDate() {
        return airDate;
    }

    public String getShortIdentifier() {
        return String.format("S%02dE%02d", seasonNumber, episodeNumber);
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public List<Person> getCrew() {
        return crew;
    }

    public void setCrew(List<Person> crew) {
        this.crew = crew;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        if (overview != null && !overview.isEmpty()) {
            return overview;
        }
        return "No episode description available.";
    }

    public boolean hasOverview() {
        return overview != null && !overview.isEmpty();
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductionCode() {
        return productionCode;
    }

    public void setProductionCode(String productionCode) {
        this.productionCode = productionCode;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getStill(EpisodeImageSize imageSize) {
        if (stillPath != null) {
            return Configuration.IMAGE_URL + imageSize.toString() + stillPath;
        }
        return null;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Episode episode = (Episode) o;
        return Objects.equals(airDate, episode.airDate) &&
                Objects.equals(episodeNumber, episode.episodeNumber) &&
                Objects.equals(name, episode.name) &&
                Objects.equals(overview, episode.overview) &&
                Objects.equals(id, episode.id) &&
                Objects.equals(productionCode, episode.productionCode) &&
                Objects.equals(seasonNumber, episode.seasonNumber) &&
                Objects.equals(stillPath, episode.stillPath) &&
                Objects.equals(voteAverage, episode.voteAverage) &&
                Objects.equals(voteCount, episode.voteCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airDate, episodeNumber, name, overview, id, productionCode, seasonNumber, stillPath, voteAverage, voteCount);
    }
}
package uk.ac.tees.tvshowapp.tmdb.model;

import android.content.Context;

import java.io.Serializable;
import java.text.DateFormat;
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
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

public class Season implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("air_date")
    @Expose
    private String airDate;
    @SerializedName("episodes")
    @Expose
    private List<Episode> episodes = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("season_number")
    @Expose
    private Integer seasonNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAirDate() {
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

    public String getAirDateString() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
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

        if (getAirDate() == null) {
            return "No description available.";
        }

        // if overview is empty then just put air date.
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date premiereDate = format.parse(getAirDateString());
            SimpleDateFormat df = new SimpleDateFormat("d MMMM yyyy");
            return getName() + " premiered on " + df.format(premiereDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getRawOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath(PosterImageSize posterImageSize) {
        if (posterPath != null) {
            return Configuration.IMAGE_URL + posterImageSize.name() + posterPath;
        }
        return null;
    }

    public String getPosterPathString() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public void ensureComplete(int tvShowId, final TVShowUpdateListener updateListener, Context context, CompositeDisposable compositeDisposable) {
        // check if already complete
        if (getEpisodes() != null) {
            updateListener.onTVShowUpdate();
            return;
        }

        // get the season and and call updateInfo when done
        Disposable disposable = MediaRepository.get(context).getSeason(tvShowId, getSeasonNumber())
                .subscribe(season -> {
                    updateSeason(season);
                    updateListener.onTVShowUpdate();
                }, throwable -> {
                    updateListener.onFailure();
                });
        compositeDisposable.add(disposable);
    }

    // copy information into this
    private void updateSeason(Season updated) {
        episodes = updated.getEpisodes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Season season = (Season) o;
        return Objects.equals(id, season.id) &&
                Objects.equals(airDate, season.airDate) &&
                Objects.equals(episodes, season.episodes) &&
                Objects.equals(name, season.name) &&
                Objects.equals(overview, season.overview) &&
                Objects.equals(posterPath, season.posterPath) &&
                Objects.equals(seasonNumber, season.seasonNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, airDate, episodes, name, overview, posterPath, seasonNumber);
    }
}
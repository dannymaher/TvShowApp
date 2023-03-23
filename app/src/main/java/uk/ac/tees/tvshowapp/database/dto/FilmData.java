package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Dto for storing film information in the database
 */
@Entity
public class FilmData {

    @PrimaryKey
    public int filmId;
    public Boolean adult;
    public String backdropPath;
    public Integer budget;
    public String homepage;
    public String imdbId;
    public String originalLanguage;
    public String originalTitle;
    public String overview;
    public Double popularity;
    public String posterPath;
    public String releaseDate;
    public Integer revenue;
    public Integer runtime;
    public String status;
    public String tagline;
    public String title;
    public Boolean video;
    public Double voteAverage;
    public Integer voteCount;

}

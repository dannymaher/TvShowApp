package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * dto used to store all tv show information
 */
@Entity
public class TVShowData{

    @PrimaryKey
    public int tvShowId;
    public String backdropPath;
    public String firstAirDate;
    public String homepage;
    public Boolean inProduction;
    public String lastAirDate;
    public String name;
    public Integer numberOfEpisodes;
    public Integer numberOfSeasons;
    public String originalLanguage;
    public String originalName;
    public String overview;
    public Double popularity;
    public String posterPath;
    public String status;
    public String type;
    public Double voteAverage;
    public Integer voteCount;
    public Integer lastEpisodeId;
    public Integer nextEpisodeId;

}

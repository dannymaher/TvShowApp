package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import static androidx.room.ForeignKey.CASCADE;


/**
 * Dto for storing episode data in the database.
 */
@Entity(primaryKeys = {"tvShowId", "seasonNumber", "episodeNumber"},
        foreignKeys = @ForeignKey(entity = SeasonData.class,
                parentColumns = {"tvShowId", "seasonNumber"},
                childColumns = {"tvShowId", "seasonNumber"},
                onDelete = CASCADE))
public class EpisodeDto {
    public int tvShowId;
    public String airDate;
    public int episodeNumber;
    public String name;
    public String overview;
    public Integer episodeId;
    public String productionCode;
    public int seasonNumber;
    public String stillPath;
    public Double voteAverage;
    public Integer voteCount;
}

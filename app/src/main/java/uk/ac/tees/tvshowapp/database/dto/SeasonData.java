package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * dto for storing season information
 */
@Entity(primaryKeys = {"tvShowId", "seasonNumber"},
        foreignKeys = @ForeignKey(entity = TVShowData.class,
                parentColumns = "tvShowId",
                childColumns = "tvShowId",
                onDelete = CASCADE))
public class SeasonData {
    public int tvShowId;
    public int seasonNumber;
    public String seasonId;
    public String airDate;
    public String name;
    public String overview;
    public String posterPath;
}

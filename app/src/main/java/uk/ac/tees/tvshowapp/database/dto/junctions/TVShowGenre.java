package uk.ac.tees.tvshowapp.database.dto.junctions;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import uk.ac.tees.tvshowapp.database.dto.TVShowData;

import static androidx.room.ForeignKey.CASCADE;

/**
 * dto to store tvshow-genre associations
 */
@Entity(primaryKeys = {"tvShowId", "genreId"}, indices = @Index("genreId"),
        foreignKeys = @ForeignKey(entity = TVShowData.class,
                parentColumns = "tvShowId",
                childColumns = "tvShowId",
                onDelete = CASCADE))
public class TVShowGenre {
    public int tvShowId;
    public int genreId;
}

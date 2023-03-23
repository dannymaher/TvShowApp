package uk.ac.tees.tvshowapp.database.dto.junctions;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import uk.ac.tees.tvshowapp.database.dto.FilmData;

import static androidx.room.ForeignKey.CASCADE;

/**
 * dto to store film-productioncountry associations
 */
@Entity(primaryKeys = {"filmId", "countryId"}, indices = @Index("countryId"),
        foreignKeys = @ForeignKey(entity = FilmData.class,
                parentColumns = "filmId",
                childColumns = "filmId",
                onDelete = CASCADE))
public class FilmProductionCountry {
    public int filmId;
    @NonNull
    public String countryId;
}

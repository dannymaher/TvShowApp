package uk.ac.tees.tvshowapp.database.dto.junctions;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import uk.ac.tees.tvshowapp.database.dto.FilmData;

import static androidx.room.ForeignKey.CASCADE;

/**
 * dto to store film-genre associations
 */
@Entity(primaryKeys = {"filmId", "genreId"}, indices = @Index("genreId"),
        foreignKeys = @ForeignKey(entity = FilmData.class,
                parentColumns = "filmId",
                childColumns = "filmId",
                onDelete = CASCADE))
public class FilmGenre {
    public int filmId;
    public int genreId;
}

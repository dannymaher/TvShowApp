package uk.ac.tees.tvshowapp.database.dto.junctions;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import uk.ac.tees.tvshowapp.database.dto.FilmData;

import static androidx.room.ForeignKey.CASCADE;

/**
 * dto to store film-language associations
 */
@Entity(primaryKeys = {"filmId", "initial"}, indices = @Index("initial"),
        foreignKeys = @ForeignKey(entity = FilmData.class,
                parentColumns = "filmId",
                childColumns = "filmId",
                onDelete = CASCADE))
public class FilmSpokenLanguage {
    public int filmId;
    @NonNull
    public String initial;
}

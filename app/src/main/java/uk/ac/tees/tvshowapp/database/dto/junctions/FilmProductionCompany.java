package uk.ac.tees.tvshowapp.database.dto.junctions;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import uk.ac.tees.tvshowapp.database.dto.FilmData;

import static androidx.room.ForeignKey.CASCADE;

/**
 * dto to store film-productioncompany associations
 */
@Entity(primaryKeys = {"filmId", "productionCompanyId"}, indices = @Index("productionCompanyId"),
        foreignKeys = @ForeignKey(entity = FilmData.class,
                parentColumns = "filmId",
                childColumns = "filmId",
                onDelete = CASCADE))
public class FilmProductionCompany {
    public int filmId;
    public int productionCompanyId;
}

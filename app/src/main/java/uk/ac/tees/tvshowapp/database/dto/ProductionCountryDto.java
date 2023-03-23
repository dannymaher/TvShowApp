package uk.ac.tees.tvshowapp.database.dto;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * dto used to represent production countries
 */
@Entity
public class ProductionCountryDto {
    @PrimaryKey
    @NonNull public String countryId;
    public String name;
}

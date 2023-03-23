package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * dto to represent genres, for tv and films
 */
@Entity
public class GenreDto {
    @PrimaryKey
    public int genreId;
    public String name;
}

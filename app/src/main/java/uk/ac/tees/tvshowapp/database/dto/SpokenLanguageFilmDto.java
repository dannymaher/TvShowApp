package uk.ac.tees.tvshowapp.database.dto;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * dto to represent languages appearing in films
 */
@Entity
public class SpokenLanguageFilmDto {
    @PrimaryKey
    @NonNull  public String initial;
    public String name;
}

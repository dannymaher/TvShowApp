package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dto.SpokenLanguageFilmDto;

/**
 * dao for accessing the spoken language information from the database
 */
@Dao
public abstract class SpokenLanguageFilmDao extends BaseDao<SpokenLanguageFilmDto> {
}

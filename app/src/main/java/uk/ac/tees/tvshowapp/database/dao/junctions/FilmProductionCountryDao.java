package uk.ac.tees.tvshowapp.database.dao.junctions;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dao.BaseDao;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCountry;

/**
 * dao for accessing the film-production country association information from the database
 */
@Dao
public abstract class FilmProductionCountryDao extends BaseDao<FilmProductionCountry> {
}

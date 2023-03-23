package uk.ac.tees.tvshowapp.database.dao.junctions;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dao.BaseDao;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCompany;

/**
 * dao for accessing the film-production company association information from the database
 */
@Dao
public abstract class FilmProductionCompanyDao extends BaseDao<FilmProductionCompany> {
}

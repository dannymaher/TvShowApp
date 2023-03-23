package uk.ac.tees.tvshowapp.database.dao.junctions;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dao.BaseDao;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmGenre;

/**
 * dao for accessing the film-genre association information from the database
 */
@Dao
public abstract class FilmGenreDao extends BaseDao<FilmGenre> {
}

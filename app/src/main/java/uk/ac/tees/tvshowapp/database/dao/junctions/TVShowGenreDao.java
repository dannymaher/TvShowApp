package uk.ac.tees.tvshowapp.database.dao.junctions;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dao.BaseDao;
import uk.ac.tees.tvshowapp.database.dto.junctions.TVShowGenre;

/**
 * dao for accessing the tvshow-genre association information from the database
 */
@Dao
public abstract class TVShowGenreDao extends BaseDao<TVShowGenre> {
}

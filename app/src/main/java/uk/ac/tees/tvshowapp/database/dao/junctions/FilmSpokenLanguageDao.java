package uk.ac.tees.tvshowapp.database.dao.junctions;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dao.BaseDao;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmSpokenLanguage;

/**
 * dao for accessing the film-language association information from the database
 */
@Dao
public abstract class FilmSpokenLanguageDao extends BaseDao<FilmSpokenLanguage> {
}

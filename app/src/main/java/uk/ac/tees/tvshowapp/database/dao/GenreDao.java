package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.dto.GenreDto;

/**
 * dao for accessing the film information from the database
 */
@Dao
public abstract class GenreDao extends BaseDao<GenreDto>{

    /**
     * @param id the id of the genre
     * @return the genre dto
     */
    @Query("SELECT * FROM GenreDto WHERE genreId = :id LIMIT 1")
    public abstract Single<GenreDto> get(int id);
}

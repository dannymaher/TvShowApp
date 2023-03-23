package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.dto.FilmData;
import uk.ac.tees.tvshowapp.database.dto.FilmDto;

/**
 * dao for accessing the film information from the database
 */
@Dao
public abstract class FilmDao extends BaseDao<FilmData> {

    /**
     * @param id the id of the film to get
     * @return the film dto
     */
    @Transaction
    @Query("SELECT * FROM FilmData WHERE filmId=:id")
    public abstract Single<FilmDto> get(int id);

    /**
     * deletes a film with the specified id
     * @param id the id of the film to delete
     */
    @Query("DELETE FROM FilmData WHERE filmId=:id")
    public abstract void delete(int id);

}

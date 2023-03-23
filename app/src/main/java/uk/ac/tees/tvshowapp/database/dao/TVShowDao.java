package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.dto.TVShowData;
import uk.ac.tees.tvshowapp.database.dto.TVShowDto;

/**
 * dao for accessing the tv show information from the database
 */
@Dao
public abstract class TVShowDao extends BaseDao<TVShowData>{

    /**
     * @param id the id of the tv show
     * @return the tv show dto
     */
    @Transaction
    @Query("SELECT * FROM TVShowData WHERE tvShowId = :id LIMIT 1")
    public abstract Single<TVShowDto> get(int id);

    /**
     * delete a tv show
     * @param id the id of the tv show to delete
     */
    @Query("DELETE FROM TVShowData WHERE tvShowId=:id")
    public abstract void delete(int id);
}

package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.dto.SeasonData;
import uk.ac.tees.tvshowapp.tmdb.model.Season;

/**
 * dao for accessing the season information from the database
 */
@Dao
public abstract class SeasonDao extends BaseDao<SeasonData> {

    /**
     * get a season of a tv show
     * @param tvShowId the id of the tv show
     * @param seasonNumber the season number
     * @return the season dto
     */
    @Query("SELECT * FROM SeasonData WHERE tvShowId=:tvShowId AND seasonNumber=:seasonNumber LIMIT 1")
    public abstract Single<SeasonData> getSeason(int tvShowId, int seasonNumber);

    /**
     * get all seasons for the specified tv show
     * @param tvShowId the id of the tv show
     * @return season dtos for all seasons of the specified tv show
     */
    @Query("SELECT * FROM SeasonData WHERE tvShowId=:tvShowId")
    public abstract Single<List<SeasonData>> getSeasonsForShow(int tvShowId);

}

package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.dto.EpisodeDto;

/**
 * dao for accessing the episode information from the database
 */
@Dao
public abstract class EpisodeDao extends BaseDao<EpisodeDto> {

    /**
     * get all the episodes for the specified season
     * @param tvShowId the id of the tv show
     * @param seasonNumber the season number
     * @return all episodes in the specified season
     */
    @Query("SELECT * FROM EpisodeDto WHERE tvShowId=:tvShowId AND seasonNumber=:seasonNumber")
    public abstract Single<List<EpisodeDto>> getEpisodes(int tvShowId, int seasonNumber);

}

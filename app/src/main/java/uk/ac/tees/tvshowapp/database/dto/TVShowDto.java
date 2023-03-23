package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import uk.ac.tees.tvshowapp.database.dto.junctions.TVShowGenre;

/**
 * dto used to retrieve tv shows with all related information (excluding seasons) in one function
 */
public class TVShowDto {
    @Embedded
    public TVShowData tvShow;

    @Relation(
            parentColumn = "tvShowId",
            entityColumn = "genreId",
            associateBy = @Junction(TVShowGenre.class)
    )
    public List<GenreDto> genres;

    @Relation(
            parentColumn = "lastEpisodeId",
            entityColumn = "episodeId"
    )
    public EpisodeDto lastEpisode;

    @Relation(
            parentColumn = "nextEpisodeId",
            entityColumn = "episodeId"
    )
    public EpisodeDto nextEpisode;
}

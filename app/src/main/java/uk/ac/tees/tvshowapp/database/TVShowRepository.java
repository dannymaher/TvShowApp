package uk.ac.tees.tvshowapp.database;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.dao.EpisodeDao;
import uk.ac.tees.tvshowapp.database.dao.GenreDao;
import uk.ac.tees.tvshowapp.database.dao.SeasonDao;
import uk.ac.tees.tvshowapp.database.dao.TVShowDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.TVShowGenreDao;
import uk.ac.tees.tvshowapp.database.dto.EpisodeDto;
import uk.ac.tees.tvshowapp.database.dto.GenreDto;
import uk.ac.tees.tvshowapp.database.dto.SeasonData;
import uk.ac.tees.tvshowapp.database.dto.TVShowData;
import uk.ac.tees.tvshowapp.database.dto.TVShowDto;
import uk.ac.tees.tvshowapp.database.dto.junctions.TVShowGenre;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Genre;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

/**
 * repository to store TVShow information in the local database.
 */
public class TVShowRepository {

    private TVShowDao tvShowDao;
    private GenreDao genreDao;
    private TVShowGenreDao tvShowGenreDao;
    private SeasonDao seasonDao;
    private EpisodeDao episodeDao;

    public TVShowRepository(AppDatabase appDatabase) {
        tvShowDao = appDatabase.tvShowDao();
        genreDao = appDatabase.genreDao();
        tvShowGenreDao = appDatabase.tvShowGenreDao();
        seasonDao = appDatabase.seasonDao();
        episodeDao = appDatabase.episodeDao();
    }

    /**
     * @param id the id of the tv show.
     * @return a single of the tv show.
     */
    public Single<TVShow> getTvShow(int id) {
        return tvShowDao.get(id)
                .map(this::toTVShow)
                .flatMap(tvShow -> seasonDao.getSeasonsForShow(tvShow.getId()).map(seasonData -> {
                    tvShow.setSeasons(toSeasons(seasonData));
                    return tvShow;
                }));
    }

    /**
     * insert a tv show into the database asynchronously
     *
     * @param tvShow the tv show to insert.
     */
    public void putTVShow(final TVShow tvShow) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            putTVShowSync(tvShow);
        });
    }

    /**
     * insert a tv show into the database synchronously
     *
     * @param tvShow the tv show to insert.
     */
    public void putTVShowSync(final TVShow tvShow) {
        // insert immediately for foreign keys to work
        TVShowData dto = fromTVShow(tvShow);
        tvShowDao.put(dto);

        // insert genres and create genre - tvshow associations
        ArrayList<GenreDto> genres = new ArrayList<>();
        ArrayList<TVShowGenre> tvShowGenres = new ArrayList<>();
        for (Genre genre : tvShow.getGenres()) {
            GenreDto genreDto = new GenreDto();
            genreDto.genreId = genre.getId();
            genreDto.name = genre.getName();
            genres.add(genreDto);

            TVShowGenre tvShowGenre = new TVShowGenre();
            tvShowGenre.genreId = genre.getId();
            tvShowGenre.tvShowId = tvShow.getId();
            tvShowGenres.add(tvShowGenre);
        }
        genreDao.put(genres);
        tvShowGenreDao.put(tvShowGenres);

        // add seasons
        if (tvShow.getSeasons() != null) {
            for (Season season : tvShow.getSeasons()) {
                SeasonData seasonData = fromSeason(tvShow.getId(), season);
                seasonDao.put(seasonData);

                if (season.getEpisodes() != null) {
                    for (Episode episode : season.getEpisodes()) {
                        episodeDao.put(fromEpisode(tvShow.getId(), episode));
                    }
                }
            }
        }

        // add last and next episodes
        if (tvShow.getLastEpisodeToAir() != null) {
            dto.lastEpisodeId = tvShow.getLastEpisodeToAir().getId();
            episodeDao.put(fromEpisode(tvShow.getId(), tvShow.getLastEpisodeToAir()));
        }
        if (tvShow.getNextEpisodeToAir() != null) {
            dto.nextEpisodeId = tvShow.getNextEpisodeToAir().getId();
            episodeDao.put(fromEpisode(tvShow.getId(), tvShow.getNextEpisodeToAir()));
        }

        // insert again to add last and next episodes
        tvShowDao.put(dto);
    }

    /**
     * @param tvShowId     the id of the tv show
     * @param seasonNumber the number of the season
     * @return a single of the season
     */
    public Single<Season> getSeason(int tvShowId, int seasonNumber) {
        return seasonDao.getSeason(tvShowId, seasonNumber)
                .map(TVShowRepository::toSeason)
                .flatMap(season -> episodeDao.getEpisodes(tvShowId, seasonNumber).map(episodeDtos -> {
                    List<Episode> episodes = toEpisodes(episodeDtos);
                    System.out.println(episodes.size());
                    season.setEpisodes(episodes);
                    return season;
                }));
    }

    /**
     * insert the season into the database or update the current asynchronously
     *
     * @param tvShowId the id of the tv show
     * @param season   the season to insert
     */
    public void putSeason(int tvShowId, Season season) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            putSeasonSync(tvShowId, season);
        });
    }

    /**
     * insert the season into the database or update the current record synchronously
     *
     * @param tvShowId the id of the tv show
     * @param season   the season to insert
     */
    public void putSeasonSync(int tvShowId, Season season) {
        SeasonData dto = fromSeason(tvShowId, season);
        seasonDao.put(dto);

        if (season.getEpisodes() != null) {
            for (Episode episode : season.getEpisodes()) {
                putEpisode(tvShowId, episode);
            }
        }
    }

    /**
     * insert an episode into the database or update the current record
     *
     * @param tvShowId the id of the tv show
     * @param episode  the episode to insert
     */
    private void putEpisode(int tvShowId, Episode episode) {
        episodeDao.put(fromEpisode(tvShowId, episode));
    }

    /**
     * delete a tv show from the database asynchronously
     *
     * @param tvShowId the id the tvshow
     */
    public void deleteTVShow(int tvShowId) {
        AppDatabase.databaseWriteExecutor.execute(() -> deleteTVShowSync(tvShowId));
    }

    /**
     * delete a tv show from the database synchronously
     *
     * @param tvShowId the id the tvshow
     */
    public void deleteTVShowSync(int tvShowId) {
        tvShowDao.delete(tvShowId);
    }

    // convert TVShowDto to tvshow
    private TVShow toTVShow(TVShowDto dto) {
        TVShow tvShow = new TVShow();
        tvShow.setId(dto.tvShow.tvShowId);
        tvShow.setBackdropPath(dto.tvShow.backdropPath);
        tvShow.setPosterPath(dto.tvShow.posterPath);
        tvShow.setInProduction(dto.tvShow.inProduction);
        tvShow.setName(dto.tvShow.name);
        tvShow.setFirstAirDate(dto.tvShow.firstAirDate);
        tvShow.setHomepage(dto.tvShow.homepage);
        tvShow.setLastAirDate(dto.tvShow.lastAirDate);
        tvShow.setOriginalLanguage(dto.tvShow.originalLanguage);
        tvShow.setNumberOfEpisodes(dto.tvShow.numberOfEpisodes);
        tvShow.setNumberOfSeasons(dto.tvShow.numberOfSeasons);
        tvShow.setOverview(dto.tvShow.overview);
        tvShow.setPopularity(dto.tvShow.popularity);
        tvShow.setOriginalName(dto.tvShow.originalName);
        tvShow.setStatus(dto.tvShow.status);
        tvShow.setType(dto.tvShow.type);
        tvShow.setVoteAverage(dto.tvShow.voteAverage);
        tvShow.setVoteCount(dto.tvShow.voteCount);

        tvShow.setGenres(toGenres(dto.genres));
        if (dto.lastEpisode != null) {
            tvShow.setLastEpisodeToAir(toEpisode(dto.lastEpisode));
        }
        if (dto.nextEpisode != null) {
            tvShow.setNextEpisodeToAir(toEpisode(dto.nextEpisode));
        }

        return tvShow;
    }

    // convert GenreDto to genre
    static List<Genre> toGenres(List<GenreDto> dtos) {
        ArrayList<Genre> genres = new ArrayList<>();
        for (GenreDto genreDto : dtos) {
            Genre genre = new Genre();
            genre.setId(genreDto.genreId);
            genre.setName(genreDto.name);
            genres.add(genre);
        }
        return genres;
    }

    // convert SeasonDatas to season
    private static List<Season> toSeasons(List<SeasonData> seasonDatas) {
        ArrayList<Season> seasons = new ArrayList<>();
        for (SeasonData seasonData : seasonDatas) {
            Season season = toSeason(seasonData);
            seasons.add(season);
        }
        return seasons;
    }

    // convert SeasonData to season
    private static Season toSeason(SeasonData seasonData) {
        Season season = new Season();
        season.setAirDate(seasonData.airDate);
        season.setName(seasonData.name);
        season.setOverview(seasonData.overview);
        season.setPosterPath(seasonData.posterPath);
        season.setSeasonNumber(seasonData.seasonNumber);
        season.setId(seasonData.seasonId);
        return season;
    }

    // Convert EpisodeDto to episode;
    private static Episode toEpisode(EpisodeDto episodeDto) {
        Episode episode = new Episode();
        episode.setAirDate(episodeDto.airDate);
        episode.setEpisodeNumber(episodeDto.episodeNumber);
        episode.setName(episodeDto.name);
        episode.setOverview(episodeDto.overview);
        episode.setId(episodeDto.episodeId);
        episode.setProductionCode(episodeDto.productionCode);
        episode.setSeasonNumber(episodeDto.seasonNumber);
        episode.setStillPath(episodeDto.stillPath);
        episode.setVoteAverage(episodeDto.voteAverage);
        episode.setVoteCount(episodeDto.voteCount);
        return episode;
    }

    // convert EpisodeDtos into episodes
    private static List<Episode> toEpisodes(List<EpisodeDto> episodeDtos) {
        ArrayList<Episode> episodes = new ArrayList<>();
        for (EpisodeDto episodeDto : episodeDtos) {
            episodes.add(toEpisode(episodeDto));
        }
        return episodes;
    }

    // convert episode to dto
    private static EpisodeDto fromEpisode(int tvShowId, Episode episode) {
        EpisodeDto dto = new EpisodeDto();
        dto.airDate = episode.getAirDate();
        dto.episodeNumber = episode.getEpisodeNumber();
        dto.name = episode.getName();
        dto.overview = episode.getOverview();
        dto.episodeId = episode.getId();
        dto.productionCode = episode.getProductionCode();
        dto.seasonNumber = episode.getSeasonNumber();
        dto.stillPath = episode.getStillPath();
        dto.voteAverage = episode.getVoteAverage();
        dto.voteCount = episode.getVoteCount();
        dto.tvShowId = tvShowId;
        return dto;
    }

    // convert Seasons into SeasonDatas
    private static SeasonData fromSeason(int tvShowId, Season season) {
        SeasonData dto = new SeasonData();
        dto.seasonId = season.getId();
        dto.name = season.getName();
        dto.tvShowId = tvShowId;
        dto.overview = season.getRawOverview();
        dto.seasonNumber = season.getSeasonNumber();
        dto.airDate = season.getAirDateString();
        dto.posterPath = season.getPosterPathString();
        return dto;
    }

    // convert TVShows into TVShowDatas
    private static TVShowData fromTVShow(TVShow tvShow) {
        TVShowData dto = new TVShowData();
        dto.tvShowId = tvShow.getId();
        dto.backdropPath = tvShow.getBackdropPath();
        dto.posterPath = tvShow.getPosterPath();
        dto.name = tvShow.getName();
        dto.firstAirDate = tvShow.getFirstAirDate();
        dto.homepage = tvShow.getHomepage();
        dto.inProduction = tvShow.getInProduction();
        dto.lastAirDate = tvShow.getLastAirDate();
        dto.numberOfEpisodes = tvShow.getNumberOfEpisodes();
        dto.numberOfSeasons = tvShow.getNumberOfSeasons();
        dto.originalLanguage = tvShow.getOriginalLanguage();
        dto.overview = tvShow.getOverview();
        dto.popularity = tvShow.getPopularity();
        dto.originalName = tvShow.getOriginalName();
        dto.status = tvShow.getStatus();
        dto.type = tvShow.getType();
        dto.voteAverage = tvShow.getVoteAverage();
        dto.voteCount = tvShow.getVoteCount();
        return dto;
    }
}

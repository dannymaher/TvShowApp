package uk.ac.tees.tvshowapp.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.ac.tees.tvshowapp.database.dao.EpisodeDao;
import uk.ac.tees.tvshowapp.database.dao.FilmDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmGenreDao;
import uk.ac.tees.tvshowapp.database.dao.GenreDao;
import uk.ac.tees.tvshowapp.database.dao.ProductionCompanyDao;
import uk.ac.tees.tvshowapp.database.dao.ProductionCountryDao;
import uk.ac.tees.tvshowapp.database.dao.SeasonDao;
import uk.ac.tees.tvshowapp.database.dao.SpokenLanguageFilmDao;
import uk.ac.tees.tvshowapp.database.dao.TVShowDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmProductionCompanyDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmProductionCountryDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmSpokenLanguageDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.TVShowGenreDao;
import uk.ac.tees.tvshowapp.database.dto.EpisodeDto;
import uk.ac.tees.tvshowapp.database.dto.FilmData;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmGenre;
import uk.ac.tees.tvshowapp.database.dto.GenreDto;
import uk.ac.tees.tvshowapp.database.dto.ProductionCompanyDto;
import uk.ac.tees.tvshowapp.database.dto.ProductionCountryDto;
import uk.ac.tees.tvshowapp.database.dto.SeasonData;
import uk.ac.tees.tvshowapp.database.dto.SpokenLanguageFilmDto;
import uk.ac.tees.tvshowapp.database.dto.TVShowData;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCompany;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCountry;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmSpokenLanguage;
import uk.ac.tees.tvshowapp.database.dto.junctions.TVShowGenre;

/**
 * access to on-disk sql database, for storing tv show and film information
 */
@androidx.room.Database(entities = {TVShowData.class, GenreDto.class, TVShowGenre.class,
        SeasonData.class, EpisodeDto.class, FilmData.class, FilmGenre.class,
        SpokenLanguageFilmDto.class, ProductionCompanyDto.class, ProductionCountryDto.class,
        FilmProductionCompany.class, FilmProductionCountry.class, FilmSpokenLanguage.class},
        version = 17)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TVShowDao tvShowDao();

    public abstract GenreDao genreDao();

    public abstract TVShowGenreDao tvShowGenreDao();

    public abstract SeasonDao seasonDao();

    public abstract EpisodeDao episodeDao();

    public abstract FilmDao filmDao();

    public abstract FilmGenreDao filmGenreDao();

    public abstract ProductionCompanyDao productionCompanyDao();

    public abstract ProductionCountryDao productionCountryDao();

    public abstract SpokenLanguageFilmDao spokenLanguageFilmDao();

    public abstract FilmProductionCompanyDao filmProductionCompanyDao();

    public abstract FilmProductionCountryDao filmProductionCountryDao();

    public abstract FilmSpokenLanguageDao filmSpokenLanguageDao();

    private static volatile AppDatabase instance;

    /**
     * Thread pool to run queries on.
     */
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * @return an instance of AppDatabase.
     */
    public static AppDatabase get(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "TVShowApp")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return  instance;
    }

}

package uk.ac.tees.tvshowapp;

import android.content.Context;

import androidx.room.EmptyResultSetException;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.AppDatabase;
import uk.ac.tees.tvshowapp.database.FilmRepository;
import uk.ac.tees.tvshowapp.database.dto.FilmDto;
import uk.ac.tees.tvshowapp.tmdb.model.Film;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TestFilmRepository {
    private AppDatabase database;
    private FilmRepository filmRepository;
    private Film exampleFilm;

    /**
     * initialise (in memory) test database, film repository and example film.
     */
    @Before
    public void setupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        filmRepository = new FilmRepository(database);

        String exampleFilmJson = "{\"adult\":false,\"backdrop_path\":\"/fCayJrkfRaCRCTh8GqN30f8oyQF.jpg\",\"belongs_to_collection\":null,\"budget\":63000000,\"genres\":[{\"id\":18,\"name\":\"Drama\"}],\"homepage\":\"\",\"id\":550,\"imdb_id\":\"tt0137523\",\"original_language\":\"en\",\"original_title\":\"Fight Club\",\"overview\":\"A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \\\"fight clubs\\\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.\",\"popularity\":0.5,\"poster_path\":null,\"production_companies\":[{\"id\":508,\"logo_path\":\"/7PzJdsLGlR7oW4J0J5Xcd0pHGRg.png\",\"name\":\"Regency Enterprises\",\"origin_country\":\"US\"},{\"id\":711,\"logo_path\":null,\"name\":\"Fox 2000 Pictures\",\"origin_country\":\"\"},{\"id\":20555,\"logo_path\":null,\"name\":\"Taurus Film\",\"origin_country\":\"\"},{\"id\":54050,\"logo_path\":null,\"name\":\"Linson Films\",\"origin_country\":\"\"},{\"id\":54051,\"logo_path\":null,\"name\":\"Atman Entertainment\",\"origin_country\":\"\"},{\"id\":54052,\"logo_path\":null,\"name\":\"Knickerbocker Films\",\"origin_country\":\"\"},{\"id\":25,\"logo_path\":\"/qZCc1lty5FzX30aOCVRBLzaVmcp.png\",\"name\":\"20th Century Fox\",\"origin_country\":\"US\"}],\"production_countries\":[{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"1999-10-12\",\"revenue\":100853753,\"runtime\":139,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"}],\"status\":\"Released\",\"tagline\":\"How much can you know about yourself if you've never been in a fight?\",\"title\":\"Fight Club\",\"video\":false,\"vote_average\":7.8,\"vote_count\":3439}";
        exampleFilm = new Gson().fromJson(exampleFilmJson, Film.class);
    }

    @Test
    public void putFilm_newFilm_dataCorrect() {
        // insert example film into databaset p
        filmRepository.putFilmSync(exampleFilm);

        // get the inserted film
        Single<FilmDto> filmDtoSingle = database.filmDao().get(exampleFilm.getId());
        FilmDto filmDto = filmDtoSingle.blockingGet();

        // check all data is correct
        assertEquals(exampleFilm.getId().intValue(), filmDto.film.filmId);
        assertEquals(exampleFilm.getAdult(), filmDto.film.adult);
        assertEquals(exampleFilm.getBudget(), filmDto.film.budget);
        assertEquals(exampleFilm.getHomepage(), filmDto.film.homepage);
        assertEquals(exampleFilm.getImdbId(), filmDto.film.imdbId);
        assertEquals(exampleFilm.getOriginalLanguage(), filmDto.film.originalLanguage);
        assertEquals(exampleFilm.getOriginalTitle(), filmDto.film.originalTitle);
        assertEquals(exampleFilm.getOverview(), filmDto.film.overview);
        assertEquals(exampleFilm.getPopularity(), filmDto.film.popularity);
        assertEquals(exampleFilm.getPosterPath(), filmDto.film.posterPath);
        assertEquals(exampleFilm.getReleaseDate(), filmDto.film.releaseDate);
        assertEquals(exampleFilm.getRevenue(), filmDto.film.revenue);
        assertEquals(exampleFilm.getRevenue(), filmDto.film.revenue);
        assertEquals(exampleFilm.getRuntime(), filmDto.film.runtime);
        assertEquals(exampleFilm.getStatus(), filmDto.film.status);
        assertEquals(exampleFilm.getTagline(), filmDto.film.tagline);
        assertEquals(exampleFilm.getTitle(), filmDto.film.title);
        assertEquals(exampleFilm.getVideo(), filmDto.film.video);
        assertEquals(exampleFilm.getVoteAverage(), filmDto.film.voteAverage);
        assertEquals(exampleFilm.getVoteCount(), filmDto.film.voteCount);
    }

    @Test
    public void putFilm_newFilm_genresCorrect() {
        // insert example film
        filmRepository.putFilmSync(exampleFilm);

        Film film = putThenGetFilm();

        assertTrue(exampleFilm.getGenres().containsAll(film.getGenres()));
        assertTrue(film.getGenres().containsAll(exampleFilm.getGenres()));
    }

    @Test
    public void putFilm_newFilm_productionCompaniesCorrect() {
        Film film = putThenGetFilm();

        assertTrue(exampleFilm.getProductionCompanies().containsAll(film.getProductionCompanies()));
        assertTrue(film.getProductionCompanies().containsAll(exampleFilm.getProductionCompanies()));
    }

    @Test
    public void putFilm_newFilm_productionCountriesCorrect() {
        Film film = putThenGetFilm();

        assertTrue(exampleFilm.getProductionCountries().containsAll(film.getProductionCountries()));
        assertTrue(film.getProductionCountries().containsAll(exampleFilm.getProductionCountries()));
    }

    @Test
    public void putFilm_newFilm_spokenLanguagesCorrect() {
        Film film = putThenGetFilm();

        assertTrue(exampleFilm.getSpokenLanguages().containsAll(film.getSpokenLanguages()));
        assertTrue(film.getSpokenLanguages().containsAll(exampleFilm.getSpokenLanguages()));
    }

    @Test(expected = EmptyResultSetException.class)
    public void deleteFilm_filmDeleted() {
        Film film = putThenGetFilm();

        filmRepository.deleteFilmSync(film.getId());

        database.filmDao().get(film.getId()).blockingGet();
    }

    /**
     * insert example film into the database then retreive it
     *
     * @return the film from the database
     */
    private Film putThenGetFilm() {
        // insert example film
        filmRepository.putFilmSync(exampleFilm);

        // get the inserted film
        Single<Film> filmSingle = filmRepository.getFilm(exampleFilm.getId());
        return filmSingle.blockingGet();
    }
}
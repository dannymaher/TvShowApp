package uk.ac.tees.tvshowapp.database;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import uk.ac.tees.tvshowapp.database.dao.FilmDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmGenreDao;
import uk.ac.tees.tvshowapp.database.dao.GenreDao;
import uk.ac.tees.tvshowapp.database.dao.ProductionCompanyDao;
import uk.ac.tees.tvshowapp.database.dao.ProductionCountryDao;
import uk.ac.tees.tvshowapp.database.dao.SpokenLanguageFilmDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmProductionCompanyDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmProductionCountryDao;
import uk.ac.tees.tvshowapp.database.dao.junctions.FilmSpokenLanguageDao;
import uk.ac.tees.tvshowapp.database.dto.FilmData;
import uk.ac.tees.tvshowapp.database.dto.FilmDto;
import uk.ac.tees.tvshowapp.database.dto.ProductionCompanyDto;
import uk.ac.tees.tvshowapp.database.dto.ProductionCountryDto;
import uk.ac.tees.tvshowapp.database.dto.SpokenLanguageFilmDto;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmGenre;
import uk.ac.tees.tvshowapp.database.dto.GenreDto;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCompany;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCountry;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmSpokenLanguage;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.Genre;
import uk.ac.tees.tvshowapp.tmdb.model.ProductionCompany;
import uk.ac.tees.tvshowapp.tmdb.model.ProductionCountry;
import uk.ac.tees.tvshowapp.tmdb.model.SpokenLanguageFilm;

/**
 * repository class to retrieve or remove films from the database.
 */
public class FilmRepository {

    private FilmDao filmDao;
    private GenreDao genreDao;
    private FilmGenreDao filmGenreDao;
    private ProductionCompanyDao productionCompanyDao;
    private ProductionCountryDao productionCountryDao;
    private SpokenLanguageFilmDao spokenLanguageFilmDao;
    private FilmProductionCompanyDao filmProductionCompanyDao;
    private FilmProductionCountryDao filmProductionCountryDao;
    private FilmSpokenLanguageDao filmSpokenLanguageDao;

    public FilmRepository(AppDatabase appDatabase) {
        filmDao = appDatabase.filmDao();
        genreDao = appDatabase.genreDao();
        filmGenreDao = appDatabase.filmGenreDao();

        productionCompanyDao = appDatabase.productionCompanyDao();
        productionCountryDao = appDatabase.productionCountryDao();
        spokenLanguageFilmDao = appDatabase.spokenLanguageFilmDao();
        filmProductionCompanyDao = appDatabase.filmProductionCompanyDao();
        filmProductionCountryDao = appDatabase.filmProductionCountryDao();
        filmSpokenLanguageDao = appDatabase.filmSpokenLanguageDao();
    }

    /**
     * @param id the id of the film.
     * @return a single of the film.
     */
    public Single<Film> getFilm(int id) {
        return filmDao.get(id).map(this::toFilm);
    }

    /**
     * insert the film or update the current record asynchronously
     *
     * @param film the film to put.
     */
    public void putFilm(Film film) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            putFilmSync(film);
        });
    }

    /**
     * insert the film or update the current record synchronously
     *
     * @param film the film to put.
     */
    public void putFilmSync(Film film) {
        filmDao.put(fromFilm(film));

        ArrayList<GenreDto> genreDtos = new ArrayList<>();
        ArrayList<FilmGenre> filmGenres = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            GenreDto genreDto = new GenreDto();
            genreDto.genreId = genre.getId();
            genreDto.name = genre.getName();
            genreDtos.add(genreDto);

            FilmGenre filmGenre = new FilmGenre();
            filmGenre.filmId = film.getId();
            filmGenre.genreId = genre.getId();
            filmGenres.add(filmGenre);
        }
        genreDao.put(genreDtos);
        filmGenreDao.put(filmGenres);

        ArrayList<ProductionCompanyDto> productionCompanyDtos = new ArrayList<>();
        ArrayList<FilmProductionCompany> filmProductionCompanies = new ArrayList<>();
        for (ProductionCompany productionCompany : film.getProductionCompanies()) {
            ProductionCompanyDto productionCompanyDto = fromProductionCompany(productionCompany);
            productionCompanyDtos.add(productionCompanyDto);

            FilmProductionCompany filmProductionCompany = new FilmProductionCompany();
            filmProductionCompany.filmId = film.getId();
            filmProductionCompany.productionCompanyId = productionCompany.getId();
            filmProductionCompanies.add(filmProductionCompany);
        }
        productionCompanyDao.put(productionCompanyDtos);
        filmProductionCompanyDao.put(filmProductionCompanies);


        ArrayList<ProductionCountryDto> productionCountryDtos = new ArrayList<>();
        ArrayList<FilmProductionCountry> filmProductionCountries = new ArrayList<>();
        for (ProductionCountry productionCountry : film.getProductionCountries()) {
            ProductionCountryDto productionCountryDto = new ProductionCountryDto();
            productionCountryDto.countryId = productionCountry.getCountryId();
            productionCountryDto.name = productionCountry.getName();
            productionCountryDtos.add(productionCountryDto);

            FilmProductionCountry filmProductionCountry = new FilmProductionCountry();
            filmProductionCountry.filmId = film.getId();
            filmProductionCountry.countryId = productionCountry.getCountryId();
            filmProductionCountries.add(filmProductionCountry);
        }
        productionCountryDao.put(productionCountryDtos);
        filmProductionCountryDao.put(filmProductionCountries);

        ArrayList<SpokenLanguageFilmDto> spokenLanguageFilmDtos = new ArrayList<>();
        ArrayList<FilmSpokenLanguage> filmSpokenLanguages = new ArrayList<>();
        for (SpokenLanguageFilm spokenLanguageFilm : film.getSpokenLanguages()) {
            SpokenLanguageFilmDto spokenLanguageFilmDto = new SpokenLanguageFilmDto();
            spokenLanguageFilmDto.initial = spokenLanguageFilm.getInitial();
            spokenLanguageFilmDto.name = spokenLanguageFilm.getName();
            spokenLanguageFilmDtos.add(spokenLanguageFilmDto);

            FilmSpokenLanguage filmSpokenLanguage = new FilmSpokenLanguage();
            filmSpokenLanguage.filmId = film.getId();
            filmSpokenLanguage.initial = spokenLanguageFilm.getInitial();
            filmSpokenLanguages.add(filmSpokenLanguage);
        }
        spokenLanguageFilmDao.put(spokenLanguageFilmDtos);
        filmSpokenLanguageDao.put(filmSpokenLanguages);
    }

    /**
     * delete a film from the database asynchronously
     *
     * @param id the id of the film to delete.
     */
    public void deleteFilm(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> deleteFilmSync(id));
    }

    /**
     * delete a film from the database synchronously
     *
     * @param id the id of the film to delete.
     */
    public void deleteFilmSync(int id) {
        filmDao.delete(id);
    }

    // convert FilmDto objects into Film objects.
    private Film toFilm(FilmDto filmDto) {
        FilmData dto = filmDto.film;

        Film film = new Film();
        film.setId(dto.filmId);
        film.setAdult(dto.adult);
        film.setBackdropPath(dto.backdropPath);
        film.setBudget(dto.budget);
        film.setGenres(TVShowRepository.toGenres(filmDto.genres));
        film.setHomepage(dto.homepage);
        film.setImdbId(dto.imdbId);
        film.setOriginalLanguage(dto.originalLanguage);
        film.setOriginalTitle(dto.originalTitle);
        film.setOverview(dto.overview);
        film.setPopularity(dto.popularity);
        film.setPosterPath(dto.posterPath);
        film.setReleaseDate(dto.releaseDate);
        film.setTagline(dto.tagline);
        film.setRevenue(dto.revenue);
        film.setRuntime(dto.runtime);
        film.setStatus(dto.status);
        film.setTitle(dto.title);
        film.setVideo(dto.video);
        film.setVoteAverage(dto.voteAverage);
        film.setVoteCount(dto.voteCount);

        film.setProductionCompanies(toProductionCompanies(filmDto.productionCompanies));

        ArrayList<ProductionCountry> productionCountries = new ArrayList<>();
        for (ProductionCountryDto productionCountryDto : filmDto.productionCountries) {
            ProductionCountry productionCountry = new ProductionCountry();
            productionCountry.setCountryId(productionCountryDto.countryId);
            productionCountry.setName(productionCountryDto.name);
            productionCountries.add(productionCountry);
        }
        film.setProductionCountries(productionCountries);

        ArrayList<SpokenLanguageFilm> spokenLanguages = new ArrayList<>();
        for (SpokenLanguageFilmDto spokenLanguageFilmDto : filmDto.spokenLanguages) {
            SpokenLanguageFilm spokenLanguageFilm = new SpokenLanguageFilm();
            spokenLanguageFilm.setInitial(spokenLanguageFilmDto.initial);
            spokenLanguageFilm.setName(spokenLanguageFilmDto.name);
            spokenLanguages.add(spokenLanguageFilm);
        }
        film.setSpokenLanguages(spokenLanguages);

        return film;
    }

    // convert Film objects into FilmData objects.
    private FilmData fromFilm(Film film) {
        FilmData dto = new FilmData();

        dto.filmId = film.getId();
        dto.adult = film.getAdult();
        dto.backdropPath = film.getBackdropPathString();
        dto.budget = film.getBudget();
        dto.homepage = film.getHomepage();
        dto.imdbId = film.getImdbId();
        dto.originalLanguage = film.getOriginalLanguage();
        dto.originalTitle = film.getTitle();
        dto.overview = film.getOverview();
        dto.popularity = film.getPopularity();
        dto.posterPath = film.getPosterPath();
        dto.releaseDate = film.getReleaseDate();
        dto.tagline = film.getTagline();
        dto.revenue = film.getRevenue();
        dto.runtime = film.getRuntime();
        dto.status = film.getStatus();
        dto.title = film.getTitle();
        dto.video = film.getVideo();
        dto.voteAverage = film.getVoteAverage();
        dto.voteCount = film.getVoteCount();
        return dto;
    }

    // convert ProductionCompany objects into ProductionCompanyDto objects.
    private ProductionCompanyDto fromProductionCompany(ProductionCompany productionCompany) {
        ProductionCompanyDto dto = new ProductionCompanyDto();
        dto.productionCompanyId = productionCompany.getId();
        dto.name = productionCompany.getName();
        dto.description = productionCompany.getDescription();
        dto.headquarters = productionCompany.getHeadquarters();
        dto.homepage = productionCompany.getHomepage();
        dto.logoPath = productionCompany.getLogoPath();
        dto.originCountry = productionCompany.getOriginCountry();
        dto.parentCompany = productionCompany.getParentCompany();
        return dto;
    }

    // Convert ProductionCompanies into ProductionCompanyDtos
    private List<ProductionCompany> toProductionCompanies(List<ProductionCompanyDto> dtos) {
        ArrayList<ProductionCompany> productionCompanies = new ArrayList<>();
        for (ProductionCompanyDto dto : dtos) {
            ProductionCompany productionCompany = new ProductionCompany();
            productionCompany.setId(dto.productionCompanyId);
            productionCompany.setName(dto.name);
            productionCompany.setDescription(dto.description);
            productionCompany.setHeadquarters(dto.headquarters);
            productionCompany.setHomepage(dto.homepage);
            productionCompany.setLogoPath(dto.logoPath);
            productionCompany.setParentCompany(dto.parentCompany);
            productionCompany.setOriginCountry(dto.originCountry);

            productionCompanies.add(productionCompany);
        }
        return productionCompanies;
    }


}

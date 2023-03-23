package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import uk.ac.tees.tvshowapp.database.dto.junctions.FilmGenre;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCompany;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmProductionCountry;
import uk.ac.tees.tvshowapp.database.dto.junctions.FilmSpokenLanguage;

/**
 * dto used to retrieve films and associated information from the database in a single function.
 */
public class FilmDto {

    @Embedded
    public FilmData film;

    @Relation(
            parentColumn = "filmId",
            entityColumn = "genreId",
            associateBy =  @Junction(FilmGenre.class)
    )
    public List<GenreDto> genres;

    @Relation(
            parentColumn = "filmId",
            entityColumn = "productionCompanyId",
            associateBy = @Junction(FilmProductionCompany.class)
    )
    public List<ProductionCompanyDto> productionCompanies;

    @Relation(
            parentColumn = "filmId",
            entityColumn = "countryId",
            associateBy = @Junction(FilmProductionCountry.class)
    )
    public List<ProductionCountryDto> productionCountries;

    @Relation(
            parentColumn = "filmId",
            entityColumn = "initial",
            associateBy = @Junction(FilmSpokenLanguage.class)
    )
    public List<SpokenLanguageFilmDto> spokenLanguages;

}

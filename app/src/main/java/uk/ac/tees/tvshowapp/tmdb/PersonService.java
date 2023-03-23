package uk.ac.tees.tvshowapp.tmdb;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import uk.ac.tees.tvshowapp.tmdb.model.CastExtrernalId;
import uk.ac.tees.tvshowapp.tmdb.model.CastImage;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.PersonCredits;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public interface PersonService {
    @GET("person/{person_id}")
    Call<Person> getPerson(@Path("person_id") int id);

    @GET("trending/person/week")
    Call<SearchResults<Person>> getTrending(@Query("page") int page);

    @GET("person/{person_id}/images")
    Call<PersonImages> getImages(@Path("person_id") int id);

    @GET("person/{person_id}/tagged_images")
    Call<SearchResults<CastImage>> getTagged(@Path("person_id") int id);

    @GET("person/{person_id}/external_ids")
    Call<CastExtrernalId> getExternalId(@Path("person_id") int id);

    @GET("person/{person_id}/movie_credits")
    Call<PersonCredits<Film>> getFilms(@Path("person_id") int id);

    @GET("person/{person_id}/movie_credits")
    Call<PersonCredits<Film>> getDirected(@Path("person_id") int id);

    @GET("person/{person_id}/tv_credits")
    Call<PersonCredits<TVShow>> getShows(@Path("person_id") int id);

    @GET("search/person")
    Call<SearchResults<Person>> searchPeople(@Query("query") String name,
                                             @Query("page") int page);

}

package uk.ac.tees.tvshowapp.tmdb;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import uk.ac.tees.tvshowapp.tmdb.model.Cast;
import uk.ac.tees.tvshowapp.tmdb.model.Credits;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.Review;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;

public interface FilmService {
    @GET("movie/{movie_id}")
    Call<Film> getFilm(@Path("movie_id") int id);

    @GET("movie/{movie_id}")
    Single<Response<Film>> getFilmSingle(@Path("movie_id") int id);

    @GET("trending/movie/week")
    Call<SearchResults<Film>> getTrending(@Query("page") int page);

    @GET("genre/movie/list")
    Call<TVService.GenresContainer> getGenres();

    @GET("movie/{movie_id}/similar")
    Call<SearchResults<Film>> getSimilar(@Path("movie_id") int id);

    @GET("movie/{movie_id}/recommendations")
    Call<SearchResults<Film>> getRecommedations(@Path("movie_id") int id);

    @GET("movie/{movie_id}/credits")
    Call<Credits> getCast(@Path("movie_id") int id);

    @GET("search/movie")
    Call<SearchResults<Film>> searchFilm(@Query("query") String name,
                                         @Query("page")int page);

    @GET("movie/{movie_id}/reviews")
    Call<SearchResults<Review>> getReviews(@Path("movie_id") int id);

    @GET("discover/movie")
    Call<SearchResults<Film>> getDiscover(@Query("sort_by") String sortBy,
                                          @Query("primary_release_date.gte") String startDate,
                                          @Query("primary_release_date.lte") String endDate,
                                          @Query("vote_average.gte") Double minVoteAverage,
                                          @Query("vote_count.gte") int minVotes,
                                          @Query("with_genres") String withGenres,
                                          @Query("page") int page);
}

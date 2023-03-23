package uk.ac.tees.tvshowapp.tmdb;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Query;
import uk.ac.tees.tvshowapp.tmdb.model.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TVService {

    @GET("tv/{tv_id}")
    Call<TVShow> getTVShow(@Path("tv_id") int id);

    @GET("tv/{tv_id}")
    Single<Response<TVShow>> getTVShowSingle(@Path("tv_id") int id);


    @GET("tv/{tv_id}/season/{season_number}")
    Call<Season> getSeason(@Path("tv_id") int tvId, @Path("season_number") int seasonNumber);

    @GET("tv/{tv_id}/season/{season_number}")
    Single<Response<Season>> getSeasonSingle(@Path("tv_id") int tvId, @Path("season_number") int seasonNumber);

    @GET("tv/{tv_id}/season/{season_number}/episode/{episode_number}")
    Call<Episode> getEpisode(@Path("tv_id") int tvId, @Path("season_number") int seasonNumber,
                             @Path("episode_number") int episodeNumber);

    @GET("trending/tv/week")
    Call<SearchResults<TVShow>> getTrending(@Query("page") int page);

    @GET("tv/{tv_id}/similar")
    Call<SearchResults<TVShow>> getSimilar(@Path("tv_id") int id);

    @GET("tv/{tv_id}/recommendations")
    Call<SearchResults<TVShow>> getRecommedations(@Path("tv_id") int id);

    @GET("tv/{tv_id}/credits")
    Call<Credits> getCast(@Path("tv_id") int id);

    @GET("search/tv")
    Call<SearchResults<TVShow>> searchTv(@Query("query") String name,
                                         @Query("page")int page);

    @GET("tv/{tv_id}/reviews")
    Call<SearchResults<Review>> getReviews(@Path("tv_id") int id);

    @GET("discover/tv")
    Call<SearchResults<TVShow>> getDiscover(@Query("sort_by") String sortBy,
                                            @Query("first_air_date.gte") String startDate,
                                            @Query("air_date.lte") String endDate,
                                            @Query("vote_average.gte") Double minVoteAverage,
                                            @Query("vote_count.gte") int minVotes,
                                            @Query("with_genres") String genres,
                                            @Query("page") int page);

    @GET("genre/tv/list")
    Call<GenresContainer> getGenres();

    // have to have this because getGenres returns an object for some reason
    class GenresContainer {
        public List<Genre> genres;
    }
}

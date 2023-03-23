package uk.ac.tees.tvshowapp.firebase;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * service to get popular in your area films and tv shows
 */
public interface LocationService {

    @GET("localtv")
    Call<List<Integer>> getPopularNearbyTvIds(@Query("location") String location,
                                              @Query("page") int page);

    @GET("localfilms")
    Call<List<Integer>> getPopularNearbyFilmIds(@Query("location") String location,
                                                @Query("page") int page);
}

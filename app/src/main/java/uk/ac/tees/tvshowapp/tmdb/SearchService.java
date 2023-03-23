package uk.ac.tees.tvshowapp.tmdb;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public interface SearchService {

    @GET("search/tv")
    Call<SearchResults<TVShow>> searchTV(@Query("query") String query);

}

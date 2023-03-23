package uk.ac.tees.tvshowapp.tmdb;

import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

/**
 * Api to deal with the Tmdb serivce
 */
public class TmdbApi {

    // constants
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "4bf3ff7670831e0e528ceb252788bd41";

    /**
     * Singleton instance of tmdbapi
     */
    private static TmdbApi instance;

    private Retrofit retrofit;

    /**
     * TV Specific Api
     */
    private TVService tvService;

    /**
     * Search Service Api
     */
    private SearchService searchService;

    /**
     * Film Service Api
     */
    private FilmService filmService;

    /**
     * Person Service Api
     */
    private PersonService personService;

    private TmdbApi() {
        retrofit = getRetrofit();

        personService = retrofit.create(PersonService.class);
        tvService = retrofit.create(TVService.class);
        searchService = retrofit.create(SearchService.class);
        filmService = retrofit.create(FilmService.class);
    }

    /**
     * Returns a retrofit instance to setup the accessing of the tmdb api
     *
     * @return a retrofit instance, setup for accessing tmdb
     */
    private Retrofit getRetrofit() {
        OkHttpClient.Builder client = new OkHttpClient().newBuilder();
        client.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    /**
     * FOR TESTING
     * Set the retrofit instance to use for accessing tmdb.
     *
     * @param retrofit the retrofit instance to use.
     */
    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;

        personService = retrofit.create(PersonService.class);
        tvService = retrofit.create(TVService.class);
        searchService = retrofit.create(SearchService.class);
        filmService = retrofit.create(FilmService.class);
    }

    /**
     * Returns an instance of TmdbApi
     *
     * @return an instance of TmdbApi
     */
    public static synchronized TmdbApi get() {
        if (instance == null) {
            instance = new TmdbApi();
        }
        return instance;
    }

    /**
     * Returns an instance of TVService
     *
     * @return an instance of TVService
     */
    public TVService getTVService() {
        return tvService;
    }

    /**
     * Returns an instance of FilmService
     *
     * @return an instance of FilmService
     */
    public FilmService getFilmService() {
        return filmService;
    }

    /**
     * Returns an instance of PersonService
     *
     * @return an instance of PersonService
     */
    public PersonService getPersonService() {
        return personService;
    }

}

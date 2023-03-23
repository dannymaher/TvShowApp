package uk.ac.tees.tvshowapp;

import android.content.Context;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import uk.ac.tees.tvshowapp.database.AppDatabase;
import uk.ac.tees.tvshowapp.database.FilmRepository;
import uk.ac.tees.tvshowapp.firebase.FirebaseApi;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

/**
 * Repository class for getting tv show or film information.
 * Attempts to pull from the TmdbApi, and if the request fails, then falls back to the database.
 * If the requested item is successfully retrieved from the api and is in the list of tracked shows / films,
 * then it is stored in the database for offline access.
 */
public class MediaRepository {

    private static MediaRepository instance;

    private uk.ac.tees.tvshowapp.database.TVShowRepository tvShowRepository;
    private FilmRepository filmRepository;
    private FirebaseApi firebase;

    /**
     * create an instance of MediaRepository with default on-disk database
     *
     * @param context any context
     */
    private MediaRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.get(context);
        tvShowRepository = new uk.ac.tees.tvshowapp.database.TVShowRepository(appDatabase);
        filmRepository = new FilmRepository(appDatabase);
        firebase = FirebaseApi.get(context);
    }


    /**
     * FOR TESTING
     * create an instance of MediaRepository with a custom appDatabase instance
     *
     * @param context     any context
     * @param appDatabase an instance of appDatabase to use for fetching tv show / film info
     */
    private MediaRepository(Context context, AppDatabase appDatabase) {
        tvShowRepository = new uk.ac.tees.tvshowapp.database.TVShowRepository(appDatabase);
        filmRepository = new FilmRepository(appDatabase);
        firebase = FirebaseApi.get(context);
    }

    /**
     * Get an instance of MediaRepository
     *
     * @param context any context
     * @return an instance of the MediaRepository
     */
    public static MediaRepository get(Context context) {
        if (instance == null) {
            instance = new MediaRepository(context);
        }
        return instance;
    }

    /**
     * FOR TESTING
     * Get an instance of mediaRepository using the specified database instance
     *
     * @param appDatabase an instance of the app database
     * @return an instance of the MediaRepository
     */
    public static MediaRepository get(Context context, AppDatabase appDatabase) {
        if (instance == null) {
            instance = new MediaRepository(context, appDatabase);
        }
        return instance;
    }

    /**
     * @param id the id of the tv show.
     * @return the tv show object.
     */
    public Single<TVShow> getTVShow(final int id) {
        return TmdbApi.get().getTVService().getTVShowSingle(id)
                .flatMap(tvShowResponse -> observer -> {
                    if (tvShowResponse.isSuccessful()) {
                        if (firebase.getTrackedShowIds().getValue().contains((long) id)) {
                            tvShowRepository.putTVShow(tvShowResponse.body());
                        }
                        observer.onSuccess(tvShowResponse.body());
                    } else {
                        observer.onError(new Throwable(Integer.toString(tvShowResponse.code())));
                    }
                })
                .onErrorResumeNext(tvShowRepository.getTvShow(id))
                .map(o -> ((TVShow) o))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param tvShowId     the id of the tv show
     * @param seasonNumber the number of the season
     * @return the season object.
     */
    public Single<Season> getSeason(int tvShowId, int seasonNumber) {
        return TmdbApi.get().getTVService().getSeasonSingle(tvShowId, seasonNumber)
                .flatMap(seasonResponse -> observer -> {
                    if (seasonResponse.isSuccessful()) {
                        if (firebase.getTrackedShowIds().getValue().contains((long) tvShowId)) {
                            tvShowRepository.putSeason(tvShowId, seasonResponse.body());
                        }
                        observer.onSuccess(seasonResponse.body());
                    } else {
                        observer.onError(new Throwable(Integer.toString(seasonResponse.code())));
                    }
                })
                .onErrorResumeNext(tvShowRepository.getSeason(tvShowId, seasonNumber))
                .map(o -> ((Season) o))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param id the film id.
     * @return the film object.
     */
    public Single<Film> getFilm(int id) {
        return TmdbApi.get().getFilmService().getFilmSingle(id)
                .flatMap(filmResponse -> observer -> {
                    if (filmResponse.isSuccessful()) {
                        if (firebase.getTrackedFilmIds().getValue().contains((long) id)) {
                            filmRepository.putFilm(filmResponse.body());
                        }
                        observer.onSuccess(filmResponse.body());
                    } else {
                        observer.onError(new Throwable(Integer.toString(filmResponse.code())));
                    }
                }).onErrorResumeNext(filmRepository.getFilm(id))
                .map(o -> (Film) o)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * insert the tv show into the database, or update the current record.
     *
     * @param tvShow the tv show to insert.
     */
    public void putTVShow(TVShow tvShow) {
        tvShowRepository.putTVShow(tvShow);
    }

    /**
     * insert the film into the database, or update the current record.
     *
     * @param film the film to insert.
     */
    public void putFilm(Film film) {
        filmRepository.putFilm(film);
    }

    /**
     * delete a tv show from the database, deletes any seasons and episodes also.
     *
     * @param id the id of the tv show to delete.
     */
    public void deleteTVShow(int id) {
        tvShowRepository.deleteTVShow(id);
    }

    /**
     * delete a film from the database.
     *
     * @param id the id of the film to delete.
     */
    public void deleteFilm(int id) {
        filmRepository.deleteFilm(id);
    }
}

package uk.ac.tees.tvshowapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import uk.ac.tees.tvshowapp.MainActivity;
import uk.ac.tees.tvshowapp.MediaRepository;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.firebase.FirebaseApi;
import uk.ac.tees.tvshowapp.fragments.SettingsFragment;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;
import uk.ac.tees.tvshowapp.tmdb.model.enums.EpisodeImageSize;
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

/**
 * Notification service checks for episode and film releases and creates notifications for them.
 * The notification time can be changed in the settings page to either day of release or day before.
 * The period of the work manager job is set to 6 hours to ensure the notifications don't come through too late.
 */
public class NotificationService extends Worker {

    private static final String WORK_ID = "notificationWork";

    private static final String FILMS_CHANNEL_ID = "filmNotifications";
    private static final String EPISODES_CHANNEL_ID = "episodeNotifications";

    private static final String TAG = "Notifications";

    private Random random = new Random();

    public NotificationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // initialise firebase to make sure tracked shows and film ids are available.
        FirebaseApi.get(getApplicationContext());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // make sure notification channel has been created;
        createNotificationChannels();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // if film notifications enabled, check for film notifications
        if (sharedPreferences.getBoolean(SettingsFragment.PREF_FILM_NOTIFICATIONS, true)) {
            checkForFilmNotifications(Integer.parseInt(sharedPreferences.getString(SettingsFragment.PREF_FILM_NOTIFICATIONS_TIME, "0")));
        }

        if (sharedPreferences.getBoolean(SettingsFragment.PREF_TVSHOW_NOTIFICATIONS, true)) {
            checkForEpisodeNotifications(Integer.parseInt(sharedPreferences.getString(SettingsFragment.PREF_TVSHOW_NOTIFICATIONS_TIME, "0")));
        }

        return Result.success();
    }

    /**
     * Checks each tracked film to see if there is any the user should be notified of releasing.
     *
     * @param daysBefore the number of days before release that the user would like the notification.
     */
    private void checkForFilmNotifications(final int daysBefore) {
        Context ctxt = getApplicationContext();

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String filmIdListJson = sharedPreferences.getString("NotifiedFilms", null);
        final List<Integer> notifiedFilmsIds;
        if (filmIdListJson != null) {
            notifiedFilmsIds = new Gson().fromJson(filmIdListJson, new TypeToken<List<Integer>>() {
            }.getType());
        } else {
            notifiedFilmsIds = new ArrayList<>();
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Long> trackedFilmIds = FirebaseApi.get(ctxt).getTrackedFilmIds().getValue();
        for (Long id : trackedFilmIds) {
            // check user has not already been notified of this release.
            if (!notifiedFilmsIds.contains(id.intValue())) {
                try {
                    Film film = MediaRepository.get(getApplicationContext()).getFilm(id.intValue()).blockingGet();

                    Date releaseDate = dateFormat.parse(film.getReleaseDate());

                    // subtract a day from the release date if notifications are set to day before
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(releaseDate);
                    cal.add(Calendar.DAY_OF_YEAR, -daysBefore);
                    releaseDate = cal.getTime();

                    if (DateUtils.isToday(releaseDate.getTime())) {
                        createNotification(film);
                        notifiedFilmsIds.add(film.getId());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    // fail
                }
            }
        }

        // update notified films
        editor.putString("NotifiedFilms", new Gson().toJson(notifiedFilmsIds));
        editor.commit();
    }

    /**
     * Checks each tracked tv show to see if there is any episodes the user should be notified of releasing.
     *
     * @param daysBefore the number of days before release that the user would like the notification.
     */
    private void checkForEpisodeNotifications(final int daysBefore) {
        Context ctxt = getApplicationContext();

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String filmIdListJson = sharedPreferences.getString("NotifiedShows", null);
        final List<Integer> notifiedEpisodeIds;
        if (filmIdListJson != null) {
            notifiedEpisodeIds = new Gson().fromJson(filmIdListJson, new TypeToken<List<Integer>>() {
            }.getType());
        } else {
            notifiedEpisodeIds = new ArrayList<>();
        }


        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<Long> trackedTvShowIds = FirebaseApi.get(ctxt).getTrackedShowIds().getValue();
        for (Long id : trackedTvShowIds) {
            try {
                TVShow tvShow = MediaRepository.get(getApplicationContext()).getTVShow(id.intValue()).blockingGet();

                Episode episode = tvShow.getNextEpisodeToAir();
                // check user has not already been notified of this release.
                if (episode != null && episode.getAirDateFormatted() != null && !notifiedEpisodeIds.contains(episode.getId())) {
                    // for testing
                    // episode.setAirDate("2020-03-23");

                    try {
                        Date releaseDate = dateFormat.parse(episode.getAirDate());

                        // subtract a day from the release date if notifications are set to day before
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(releaseDate);
                        cal.add(Calendar.DAY_OF_YEAR, -daysBefore);
                        releaseDate = cal.getTime();

                        if (DateUtils.isToday(releaseDate.getTime())) {
                            createNotification(tvShow, episode);
                            notifiedEpisodeIds.add(episode.getId());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // update notified episodes
        editor.putString("NotifiedShows", new Gson().toJson(notifiedEpisodeIds));
        editor.commit();
    }

    /**
     * Create a notification for a film release
     *
     * @param film The Subject Film
     */
    private void createNotification(Film film) {
        Context ctxt = getApplicationContext();

        Intent intent = new Intent(ctxt, MainActivity.class);
        intent.setAction("uk.ac.tees.tvshowapp.film_details");
        intent.putExtra("FilmId", film.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctxt, random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // get description of when the film releases, e.g. today, tomorrow, etc.
        String description = "releases " + film.getReleaseDateFormatted();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(film.getReleaseDate());
            if (DateUtils.isToday(date.getTime())) {
                description = "releases today";
            } else {
                description = "releases " + DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctxt, FILMS_CHANNEL_ID)
                .setContentTitle(film.getTitle())
                .setContentText(description)
                // TODO: change icon
                .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                .setColor(ctxt.getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        // load film poster to display in notification
        Bitmap bitmap = null;
        try {
            if (film.getPosterPath() != null) {
                bitmap = Picasso.get().load(film.getPoster(PosterImageSize.w185)).get();
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to load film notification image.");
        }

        if (bitmap != null) {
            builder.setLargeIcon(bitmap);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctxt);
        notificationManager.notify(random.nextInt(), builder.build());
    }

    /**
     * Create a notification for a TV Show Episode Release
     *
     * @param tvShow  The Subject TV Show
     * @param episode The next episode
     */
    private void createNotification(TVShow tvShow, Episode episode) {
        Context ctxt = getApplicationContext();

        Intent intent = new Intent(ctxt, MainActivity.class);
        intent.setAction("uk.ac.tees.tvshowapp.episode_details");
        intent.putExtra("tvShowId", tvShow.getId());
        intent.putExtra("tvShowName", tvShow.getName());
        intent.putExtra("seasonNumber", episode.getSeasonNumber());
        intent.putExtra("episodeNumber", episode.getEpisodeNumber());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctxt, random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // get description of when the film releases, e.g. today, tomorrow, etc.
        String description = episode.getShortIdentifier() + " \"" + episode.getName() + "\" releases ";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(episode.getAirDate());
            description += (String) DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS);
        } catch (ParseException e) {
            description += episode.getAirDateFormatted();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctxt, EPISODES_CHANNEL_ID)
                .setContentTitle(tvShow.getName() + " - New Episode")
                .setContentText(description)
                // TODO: change icon
                .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                .setColor(ctxt.getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // load episode / tv show image to display in notification
        Bitmap bitmap = null;
        try {
            if (tvShow.getPosterPath() != null) {
                bitmap = Picasso.get().load(tvShow.getPoster(PosterImageSize.w185)).get();
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to load notification image.");
        }

        if (bitmap != null) {
            builder.setLargeIcon(bitmap);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctxt);
        notificationManager.notify(random.nextInt(), builder.build());
    }

    /**
     * Create the notification channels
     */
    private void createNotificationChannels() {
        Context ctxt = getApplicationContext();

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctxt.getString(R.string.film_notifications_channel_name);
            String description = ctxt.getString(R.string.film_notifications_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(FILMS_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = ctxt.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            name = ctxt.getString(R.string.episode_notifications_channel_name);
            description = ctxt.getString(R.string.episode_notifications_channel_description);
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(EPISODES_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager = ctxt.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Schedule the notification service to run.
     *
     * @param context any context
     */
    public static void scheduleService(final Context context) {
        // run in background as can cause freezing;
        new Thread(() -> {
            PeriodicWorkRequest periodicWorkRequest =
                    new PeriodicWorkRequest.Builder(NotificationService.class,
                            6,
                            TimeUnit.HOURS)
                            .build();

            WorkManager workManager = WorkManager.getInstance(context);
            workManager.enqueueUniquePeriodicWork(WORK_ID, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
        }).start();
    }
}

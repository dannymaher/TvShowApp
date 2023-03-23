package uk.ac.tees.tvshowapp.util;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Film;

/**
 * Handles the adding of events to the calendar
 */
public class CalendarEvents {

    /**
     * Create an intent to insert a film release into the calendar_menu
     *
     * @param film    the film to create the event for
     * @param context any context
     * @return true if sucessfully created the intent
     */
    public static boolean insertFilmEvent(Film film, Context context) {
        return insertEvent(film.getTitle() + " release", "", film.getReleaseDate(), context);
    }

    /**
     * Create an intent to insert a episode release into the calendar_menu
     *
     * @param tvShowName name of the tvShow
     * @param episode    the episode to create the event for
     * @param context    any context
     * @return true if sucessfully created the intent
     */
    public static boolean insertEpisodeEvent(String tvShowName, Episode episode, Context context) {
        String title = tvShowName + " - " + episode.getShortIdentifier();
        String description = String.format("\"%s\" releases", episode.getName());
        return insertEvent(title, description, episode.getAirDate(), context);
    }

    /**
     * Insert an event with the provided information
     *
     * @param title       event Title
     * @param description event description
     * @param dateString  date of the event
     * @param context     any context
     * @return whether the insert succeeded or failed
     */
    private static boolean insertEvent(String title, String description, String dateString, Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // set to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 1);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // create insert to calendar_menu intent
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(CalendarContract.Events.TITLE, title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, description);
            context.startActivity(intent);

        } catch (ParseException e) {
            Log.w("CalendarEvents", "invalid date string.");
            return false;
        }
        return true;
    }
}

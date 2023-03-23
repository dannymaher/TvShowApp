package uk.ac.tees.tvshowapp.util;

import android.os.Bundle;
import android.view.View;

import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

import static uk.ac.tees.tvshowapp.fragments.EpisodeDetailsFragment.ARG_EPISODE;
import static uk.ac.tees.tvshowapp.fragments.FilmDetailsFragment.ARG_FILM;
import static uk.ac.tees.tvshowapp.fragments.PersonDetailsFragment.ARG_PERSON;
import static uk.ac.tees.tvshowapp.fragments.TVShowDetailsFragment.ARG_TVSHOW;

/**
 * Navigation Utilities
 */
public class NavigationUtil {

    /**
     * Navigate to a tv show with animation
     *
     * @param view   any view in the layout
     * @param tvShow the tv show to display details of
     */
    public static void navigateToShowDetails(View view, TVShow tvShow) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TVSHOW, tvShow);
        bundle.putSerializable("title", tvShow.getName());
        NavOptions navOptions = getAnimationNavOptions();
        Navigation.findNavController(view).navigate(R.id.TVShowDetails, bundle, navOptions);
    }

    /**
     * Navigate to a film with animation
     *
     * @param view any view in the layout
     * @param film the film to display details of
     */
    public static void navigateToFilmDetails(View view, Film film) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_FILM, film);
        bundle.putSerializable("title", film.getTitle());
        NavOptions navOptions = getAnimationNavOptions();
        Navigation.findNavController(view).navigate(R.id.FilmDetails, bundle, navOptions);
    }

    /**
     * Navigate to a person with animation
     *
     * @param view   any view in the layout
     * @param person the person to display details of
     */
    public static void navigateToPersonDetails(View view, Person person) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PERSON, person);
        bundle.putSerializable("title", person.getName());
        NavOptions navOptions = getAnimationNavOptions();
        Navigation.findNavController(view).navigate(R.id.PersonDetails, bundle, navOptions);
    }

    /**
     * Navigate to an episode with animation
     *
     * @param view       any view in the layout
     * @param episode    the episode to display details of
     * @param tvShowName name of the TV Show
     */
    public static void navigateToEpisodeDetails(View view, String tvShowName, Episode episode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EPISODE, episode);
        bundle.putSerializable("tvShowName", tvShowName);
        NavOptions navOptions = getAnimationNavOptions();
        Navigation.findNavController(view).navigate(R.id.EpisodeDetails, bundle, navOptions);
    }

    /**
     * Get Nav Options with animation
     *
     * @return The Navigation Options
     */
    private static NavOptions getAnimationNavOptions() {
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.fragment_open_enter)
                .setExitAnim(R.anim.fragment_open_exit)
                .setPopEnterAnim(R.anim.fragment_close_enter)
                .setPopExitAnim(R.anim.fragment_close_exit)
                .build();
    }
}

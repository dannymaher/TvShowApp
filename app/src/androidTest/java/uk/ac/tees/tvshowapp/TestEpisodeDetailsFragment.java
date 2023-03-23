package uk.ac.tees.tvshowapp;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.tees.tvshowapp.fragments.EpisodeDetailsFragment;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TestEpisodeDetailsFragment {
    private static final String EPISODE_NAME = "episode name";
    private static final String EPISODE_DATE = "2020-01-01";
    private static final String EPISODE_OVERVIEW = "episode overview";

    private Episode episode;

    @Before
    public void setupFragment() {
        episode = new Episode();
        episode.setName(EPISODE_NAME);
        episode.setAirDate(EPISODE_DATE);
        episode.setOverview(EPISODE_OVERVIEW);

        Bundle args = new Bundle();
        args.putString("tvShowName", "TVSHOWNAME");
        args.putSerializable("episode", episode);

        FragmentScenario.launchInContainer(EpisodeDetailsFragment.class, args);
    }

    @Test
    public void titleCorrect() {
        onView(withId(R.id.episode_name))
                .check(matches(withText(episode.getName())));
    }

    @Test
    public void dateCorrect() {
        onView(withId(R.id.episode_date))
                .check(matches(withText(episode.getAirDateFormatted())));
    }

    @Test
    public void overviewCorrect() {
        onView(withId(R.id.episode_overview))
                .check(matches(withText(episode.getOverview())));
    }


}

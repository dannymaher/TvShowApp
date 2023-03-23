package uk.ac.tees.tvshowapp;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import uk.ac.tees.tvshowapp.fragments.DiscoverTVFilteringFragment;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.Genre;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TestDiscoverTVFilteringFragment {

    @Before
    public void setupFragment() {
        TmdbApi.get().setRetrofit(TmdbMock.getMockRetrofit());

        FragmentScenario.launchInContainer(DiscoverTVFilteringFragment.class);
    }

    /**
     * test genres are displayed correctly
     */
    @Test
    public void genresCorrect() throws InterruptedException {
        Thread.sleep(300);
        List<Genre> genres = TmdbMock.getGenres();

        for(int i = 0; i < genres.size(); i++){
            Genre genre = genres.get(i);

            onView(withText(genre.getName()))
                    .check(matches(isEnabled()));
        }
    }
}

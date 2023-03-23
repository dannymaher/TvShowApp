package uk.ac.tees.tvshowapp;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import uk.ac.tees.tvshowapp.fragments.FilmDetailsFragment;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.Cast;
import uk.ac.tees.tvshowapp.tmdb.model.Credits;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.ProductionCompany;
import uk.ac.tees.tvshowapp.tmdb.model.ProductionCountry;
import uk.ac.tees.tvshowapp.tmdb.model.SpokenLanguageFilm;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

public class TestFilmDetailsFragment {

    private Film exampleFilm;

    @Before
    public void setupFragment() {
        TmdbApi.get().setRetrofit(TmdbMock.getMockRetrofit());

        String exampleFilmJson = "{\"adult\":false,\"backdrop_path\":\"/fCayJrkfRaCRCTh8GqN30f8oyQF.jpg\",\"belongs_to_collection\":null,\"budget\":63000000,\"genres\":[{\"id\":18,\"name\":\"Drama\"}],\"homepage\":\"\",\"id\":550,\"imdb_id\":\"tt0137523\",\"original_language\":\"en\",\"original_title\":\"Fight Club\",\"overview\":\"A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \\\"fight clubs\\\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.\",\"popularity\":0.5,\"poster_path\":null,\"production_companies\":[{\"id\":508,\"logo_path\":\"/7PzJdsLGlR7oW4J0J5Xcd0pHGRg.png\",\"name\":\"Regency Enterprises\",\"origin_country\":\"US\"},{\"id\":711,\"logo_path\":null,\"name\":\"Fox 2000 Pictures\",\"origin_country\":\"\"},{\"id\":20555,\"logo_path\":null,\"name\":\"Taurus Film\",\"origin_country\":\"\"},{\"id\":54050,\"logo_path\":null,\"name\":\"Linson Films\",\"origin_country\":\"\"},{\"id\":54051,\"logo_path\":null,\"name\":\"Atman Entertainment\",\"origin_country\":\"\"},{\"id\":54052,\"logo_path\":null,\"name\":\"Knickerbocker Films\",\"origin_country\":\"\"},{\"id\":25,\"logo_path\":\"/qZCc1lty5FzX30aOCVRBLzaVmcp.png\",\"name\":\"20th Century Fox\",\"origin_country\":\"US\"}],\"production_countries\":[{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"1999-10-12\",\"revenue\":100853753,\"runtime\":139,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"}],\"status\":\"Released\",\"tagline\":\"How much can you know about yourself if you've never been in a fight?\",\"title\":\"Fight Club\",\"video\":false,\"vote_average\":7.8,\"vote_count\":3439}";
        exampleFilm = new Gson().fromJson(exampleFilmJson, Film.class);

        Bundle args = new Bundle();
        args.putSerializable("film", exampleFilm);
        args.putString("title", exampleFilm.getTitle());

        FragmentScenario.launchInContainer(FilmDetailsFragment.class, args);
    }

    /**
     * test the title is displayed correctly
     */
    @Test
    public void titleCorrect() {
        onView(withId(R.id.film_name))
                .check(matches(withText(exampleFilm.getTitle())));
    }

    /**
     * test the date is displayed correctly
     */
    @Test
    public void dateCorrect() {
        onView(withId(R.id.film_dates))
                .check(matches(withText(exampleFilm.getReleaseDateFormatted())));
    }

    /**
     * test the runtime is displayed correctly
     */
    @Test
    public void runtimeCorrect() {
        onView(withId(R.id.film_runtime))
                .check(matches(withText(exampleFilm.getRuntime() + " minutes")));
    }

    /**
     * test the overview is displayed correctly
     */
    @Test
    public void overviewCorrect() {
        onView(withId(R.id.film_overview))
                .check(matches(withText(exampleFilm.getOverview())));
    }

    /**
     * test the production countries are displayed correctly
     */
    @Test
    public void producedInCorrect() {
        for (ProductionCountry country : exampleFilm.getProductionCountries()) {
            onView(withText(country.getName()))
                    .check(matches(isEnabled()));
        }
    }

    /**
     * test the production companies are displayed correctly
     */
    @Test
    public void producedByCorrect() {
        for (ProductionCompany company : exampleFilm.getProductionCompanies()) {
            onView(withText(company.getName()))
                    .check(matches(isEnabled()));
        }
    }

    /**
     * test the languages are displayed correctly
     */
    @Test
    public void languagesCorrect() {
        for (SpokenLanguageFilm spokenLanguageFilm : exampleFilm.getSpokenLanguages()) {
            onView(withText(containsString(spokenLanguageFilm.getName())))
                    .check(matches(isEnabled()));
        }
    }

    /**
     * test film cast is displayed correctly
     */
    @Test
    public void castCorrect() throws InterruptedException {
        // have to sleep otherwise fails to scroll?
        Thread.sleep(300);

        Credits credits = TmdbMock.getCredits();

        onView(withId(R.id.cast_container))
                .perform(scrollTo());

        for(int i = 0; i < credits.getCast().size(); i++){
            Cast cast = credits.getCast().get(i);

            onView(withId(R.id.cast_container))
                    .perform(RecyclerViewActions.scrollToPosition(i));

            onView(withText(cast.getName()))
                    .check(matches(isDisplayed()));

            onView(withText(cast.getCharacter()))
                    .check(matches(isDisplayed()));
        }
    }
}

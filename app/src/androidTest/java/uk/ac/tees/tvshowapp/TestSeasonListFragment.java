package uk.ac.tees.tvshowapp;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import uk.ac.tees.tvshowapp.fragments.SeasonListFragment;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TestSeasonListFragment {

    private TVShow exampleShow;

    @Before
    public void setupFragment() {
        String exampleShowJson = "{\"backdrop_path\":\"/gX8SYlnL9ZznfZwEH4KJUePBFUM.jpg\",\"created_by\":[{\"id\":9813,\"credit_id\":\"5256c8c219c2956ff604858a\",\"name\":\"David Benioff\",\"gender\":2,\"profile_path\":\"/8CuuNIKMzMUL1NKOPv9AqEwM7og.jpg\"},{\"id\":228068,\"credit_id\":\"552e611e9251413fea000901\",\"name\":\"D. B. Weiss\",\"gender\":2,\"profile_path\":\"/caUAtilEe06OwOjoQY3B7BgpARi.jpg\"}],\"episode_run_time\":[60],\"first_air_date\":\"2011-04-17\",\"genres\":[{\"id\":10765,\"name\":\"Sci-Fi & Fantasy\"},{\"id\":18,\"name\":\"Drama\"},{\"id\":10759,\"name\":\"Action & Adventure\"}],\"homepage\":\"http://www.hbo.com/game-of-thrones\",\"id\":1399,\"in_production\":true,\"languages\":[\"es\",\"en\",\"de\"],\"last_air_date\":\"2017-08-27\",\"last_episode_to_air\":{\"air_date\":\"2017-08-27\",\"episode_number\":7,\"id\":1340528,\"name\":\"The Dragon and the Wolf\",\"overview\":\"A meeting is held in King's Landing. Problems arise in the North.\",\"production_code\":\"707\",\"season_number\":7,\"show_id\":1399,\"still_path\":\"/jLe9VcbGRDUJeuM8IwB7VX4GDRg.jpg\",\"vote_average\":9.145,\"vote_count\":31},\"name\":\"Game of Thrones\",\"next_episode_to_air\":null,\"networks\":[{\"name\":\"HBO\",\"id\":49,\"logo_path\":\"/tuomPhY2UtuPTqqFnKMVHvSb724.png\",\"origin_country\":\"US\"}],\"number_of_episodes\":67,\"number_of_seasons\":7,\"origin_country\":[\"US\"],\"original_language\":\"en\",\"original_name\":\"Game of Thrones\",\"overview\":\"Seven noble families fight for control of the mythical land of Westeros. Friction between the houses leads to full-scale war. All while a very ancient evil awakens in the farthest north. Amidst the war, a neglected military order of misfits, the Night's Watch, is all that stands between the realms of men and icy horrors beyond.\",\"popularity\":53.516,\"poster_path\":\"/gwPSoYUHAKmdyVywgLpKKA4BjRr.jpg\",\"production_companies\":[{\"id\":76043,\"logo_path\":\"/9RO2vbQ67otPrBLXCaC8UMp3Qat.png\",\"name\":\"Revolution Sun Studios\",\"origin_country\":\"US\"},{\"id\":3268,\"logo_path\":\"/tuomPhY2UtuPTqqFnKMVHvSb724.png\",\"name\":\"HBO\",\"origin_country\":\"US\"},{\"id\":12525,\"logo_path\":null,\"name\":\"Television 360\",\"origin_country\":\"\"},{\"id\":5820,\"logo_path\":null,\"name\":\"Generator Entertainment\",\"origin_country\":\"\"},{\"id\":12526,\"logo_path\":null,\"name\":\"Bighead Littlehead\",\"origin_country\":\"\"}],\"seasons\":[{\"air_date\":\"2010-12-05\",\"episode_count\":14,\"id\":3627,\"name\":\"Specials\",\"overview\":\"\",\"poster_path\":\"/kMTcwNRfFKCZ0O2OaBZS0nZ2AIe.jpg\",\"season_number\":0},{\"air_date\":\"2011-04-17\",\"episode_count\":10,\"id\":3624,\"name\":\"Season 1\",\"overview\":\"Trouble is brewing in the Seven Kingdoms of Westeros. For the driven inhabitants of this visionary world, control of Westeros' Iron Throne holds the lure of great power. But in a land where the seasons can last a lifetime, winter is coming...and beyond the Great Wall that protects them, an ancient evil has returned. In Season One, the story centers on three primary areas: the Stark and the Lannister families, whose designs on controlling the throne threaten a tenuous peace; the dragon princess Daenerys, heir to the former dynasty, who waits just over the Narrow Sea with her malevolent brother Viserys; and the Great Wall--a massive barrier of ice where a forgotten danger is stirring.\",\"poster_path\":\"/zwaj4egrhnXOBIit1tyb4Sbt3KP.jpg\",\"season_number\":1},{\"air_date\":\"2012-04-01\",\"episode_count\":10,\"id\":3625,\"name\":\"Season 2\",\"overview\":\"The cold winds of winter are rising in Westeros...war is coming...and five kings continue their savage quest for control of the all-powerful Iron Throne. With winter fast approaching, the coveted Iron Throne is occupied by the cruel Joffrey, counseled by his conniving mother Cersei and uncle Tyrion. But the Lannister hold on the Throne is under assault on many fronts. Meanwhile, a new leader is rising among the wildings outside the Great Wall, adding new perils for Jon Snow and the order of the Night's Watch.\",\"poster_path\":\"/5tuhCkqPOT20XPwwi9NhFnC1g9R.jpg\",\"season_number\":2},{\"air_date\":\"2013-03-31\",\"episode_count\":10,\"id\":3626,\"name\":\"Season 3\",\"overview\":\"Duplicity and treachery...nobility and honor...conquest and triumph...and, of course, dragons. In Season 3, family and loyalty are the overarching themes as many critical storylines from the first two seasons come to a brutal head. Meanwhile, the Lannisters maintain their hold on King's Landing, though stirrings in the North threaten to alter the balance of power; Robb Stark, King of the North, faces a major calamity as he tries to build on his victories; a massive army of wildlings led by Mance Rayder march for the Wall; and Daenerys Targaryen--reunited with her dragons--attempts to raise an army in her quest for the Iron Throne.\",\"poster_path\":\"/qYxRy8ZYCo2yTz7HsO6J1HWtPsY.jpg\",\"season_number\":3},{\"air_date\":\"2014-04-06\",\"episode_count\":10,\"id\":3628,\"name\":\"Season 4\",\"overview\":\"The War of the Five Kings is drawing to a close, but new intrigues and plots are in motion, and the surviving factions must contend with enemies not only outside their ranks, but within.\",\"poster_path\":\"/dniQ7zw3mbLJkd1U0gdFEh4b24O.jpg\",\"season_number\":4},{\"air_date\":\"2015-04-12\",\"episode_count\":10,\"id\":62090,\"name\":\"Season 5\",\"overview\":\"The War of the Five Kings, once thought to be drawing to a close, is instead entering a new and more chaotic phase. Westeros is on the brink of collapse, and many are seizing what they can while the realm implodes, like a corpse making a feast for crows.\",\"poster_path\":\"/527sR9hNDcgVDKNUE3QYra95vP5.jpg\",\"season_number\":5},{\"air_date\":\"2016-04-24\",\"episode_count\":10,\"id\":71881,\"name\":\"Season 6\",\"overview\":\"Following the shocking developments at the conclusion of season five, survivors from all parts of Westeros and Essos regroup to press forward, inexorably, towards their uncertain individual fates. Familiar faces will forge new alliances to bolster their strategic chances at survival, while new characters will emerge to challenge the balance of power in the east, west, north and south.\",\"poster_path\":\"/zvYrzLMfPIenxoq2jFY4eExbRv8.jpg\",\"season_number\":6},{\"air_date\":\"2017-07-16\",\"episode_count\":7,\"id\":81266,\"name\":\"Season 7\",\"overview\":\"The long winter is here. And with it comes a convergence of armies and attitudes that have been brewing for years.\",\"poster_path\":\"/3dqzU3F3dZpAripEx9kRnijXbOj.jpg\",\"season_number\":7}],\"status\":\"Returning Series\",\"type\":\"Scripted\",\"vote_average\":8.2,\"vote_count\":4682}";
        exampleShow = new Gson().fromJson(exampleShowJson, TVShow.class);

        Bundle args = new Bundle();
        args.putSerializable("tvShow", exampleShow);
        args.putString("tvShowName", exampleShow.getName());

        FragmentScenario.launchInContainer(SeasonListFragment.class, args);
    }

    @Test
    public void seasonsDisplayedCorrectly() {
        for (int i = 0; i < exampleShow.getSeasons().size(); i++) {
            Season season = exampleShow.getSeasons().get(i);

            // scroll to season details
            onView(withId(R.id.season_list))
                    .perform(RecyclerViewActions.scrollToPosition(i));

            // check if name and overview are displayed
            onView(withText(season.getName()))
                    .check(matches(isDisplayed()));
            onView(withText(season.getOverview()))
                    .check(matches(isDisplayed()));
        }
    }
}

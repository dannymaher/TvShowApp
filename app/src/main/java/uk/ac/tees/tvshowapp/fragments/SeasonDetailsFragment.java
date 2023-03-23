package uk.ac.tees.tvshowapp.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.adapters.SeasonDetailsAdapter;
import uk.ac.tees.tvshowapp.adapters.SeasonListAdapter;
import uk.ac.tees.tvshowapp.adapters.TVListAdapter;
import uk.ac.tees.tvshowapp.tmdb.listeners.TVShowUpdateListener;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public class SeasonDetailsFragment extends Fragment {
    public static final String ARG_TVSHOWNAME = "tvShowName";
    public static final String ARG_TVSHOWID = "tvShowId";
    public static final String ARG_SEASON = "season";

    private int tvShowId;
    private String tvShowName;
    private Season season;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SeasonDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SeasonDetailsFragmentArgs seasonDetailsFragmentArgs = SeasonDetailsFragmentArgs.fromBundle(getArguments());
        tvShowId = seasonDetailsFragmentArgs.getTvShowId();
        tvShowName = seasonDetailsFragmentArgs.getTvShowName();
        season = seasonDetailsFragmentArgs.getSeason();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_season_list, container, false);

        season.ensureComplete(tvShowId, new TVShowUpdateListener() {
            @Override
            public void onTVShowUpdate() {
                RecyclerView seasonListView = view.findViewById(R.id.season_list);
                seasonListView.setHasFixedSize(true);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                seasonListView.setLayoutManager(layoutManager);

                RecyclerView.Adapter seasonDetailsAdapter = new SeasonDetailsAdapter(season, new SeasonDetailsAdapter.OnEpisodeSelectedListener() {
                    @Override
                    public void onEpisodeSelected(Episode episode) {
                        NavDirections action = SeasonDetailsFragmentDirections.actionSeasonDetailsToEpisodeDetailsFragment(episode, tvShowName);
                        Navigation.findNavController(view).navigate(action);
                    }
                });
                seasonListView.setAdapter(seasonDetailsAdapter);

                if (season.getEpisodes() == null || season.getEpisodes().size() == 0) {
                    Snackbar.make(view, "Failed to load season details, check your network.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure() {
                Snackbar.make(view, "Failed to load season details, check your network.", Snackbar.LENGTH_LONG).show();
            }
        }, getContext(), compositeDisposable);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}

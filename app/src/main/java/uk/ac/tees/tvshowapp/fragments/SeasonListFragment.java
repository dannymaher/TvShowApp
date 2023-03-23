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

import java.io.Serializable;
import java.util.List;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.adapters.SeasonListAdapter;
import uk.ac.tees.tvshowapp.adapters.TVListAdapter;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public class SeasonListFragment extends Fragment {
    private static final String ARG_TVSHOW = "tvShow";

    private TVShow tvShow;

    public SeasonListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvShow = SeasonListFragmentArgs.fromBundle(getArguments()).getTvShow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_season_list, container, false);

        RecyclerView seasonListView = view.findViewById(R.id.season_list);
        seasonListView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        seasonListView.setLayoutManager(layoutManager);

        RecyclerView.Adapter seasonListAdapter = new SeasonListAdapter(tvShow.getSeasons(), new SeasonListAdapter.OnSeasonSelectedListener() {
            @Override
            public void onSeasonSelected(Season season) {
                NavDirections action = SeasonListFragmentDirections.actionSeasonListToSeasonDetailsFragment(season, tvShow.getId(), tvShow.getName());
                Navigation.findNavController(view).navigate(action);
            }
        });
        seasonListView.setAdapter(seasonListAdapter);

        return view;
    }
}

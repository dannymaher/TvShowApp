package uk.ac.tees.tvshowapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import uk.ac.tees.tvshowapp.adapters.GridSpacingItemDecoration;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.adapters.EndlessRecyclerViewScrollListener;
import uk.ac.tees.tvshowapp.adapters.FilmListAdapter;

import uk.ac.tees.tvshowapp.tmdb.model.Film;


import static uk.ac.tees.tvshowapp.fragments.FilmDetailsFragment.ARG_FILM;

/**
 * for displaying a grid of films
 */
public class FilmListFragment extends Fragment {
    private static final String ARG_FILMS = "filmList";

    private List<Film> films;
    private FilmListAdapter adapter;
    private TVListFragment.ContentLoader contentLoader;
    private EndlessRecyclerViewScrollListener scrollListener;

    public FilmListFragment() {
        // Required empty public constructor
    }

    public static FilmListFragment newInstance(List<Film> Films) {
        FilmListFragment fragment = new FilmListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILMS, (Serializable) Films);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            films = (List<Film>) getArguments().getSerializable(ARG_FILMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filmlist, container, false);

        RecyclerView filmListView = view.findViewById(R.id.filmList);
        filmListView.setHasFixedSize(true);

        GridLayoutManager filmListLayoutManager;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            filmListLayoutManager = new GridLayoutManager(getContext(), 2);
            filmListView.addItemDecoration(new GridSpacingItemDecoration(2, 24, true));
        } else {
            filmListLayoutManager = new GridLayoutManager(getContext(), 4);
            filmListView.addItemDecoration(new GridSpacingItemDecoration(4, 12, true));
        }
        filmListView.setLayoutManager(filmListLayoutManager);

        RecyclerView.Adapter FilmListAdapter = new FilmListAdapter(films, new FilmListAdapter.onFilmSelectedListener() {
            @Override
            public void onFilmSelected(Film film) {
                // go to film details
                Bundle bundle = new Bundle();
                bundle.putSerializable(ARG_FILM, film);
                bundle.putSerializable("title", film.getTitle());

                NavOptions navOptions = new NavOptions.Builder()
                        .setEnterAnim(R.anim.fragment_open_enter)
                        .setExitAnim(R.anim.fragment_open_exit)
                        .setPopEnterAnim(R.anim.fragment_close_enter)
                        .setPopExitAnim(R.anim.fragment_close_exit)
                        .build();

                Navigation.findNavController(view).navigate(R.id.FilmDetails, bundle, navOptions);
            }
        }, false);
        filmListView.setAdapter(FilmListAdapter);
        adapter = (FilmListAdapter) FilmListAdapter;

        scrollListener = new EndlessRecyclerViewScrollListener(filmListLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (contentLoader != null) {
                    contentLoader.onLoadMore(page, totalItemsCount, view);
                }
            }
        };

        filmListView.addOnScrollListener(scrollListener);

        return view;
    }

    /**
     * re-draw the recylerview, call this when the data has been completely changed
     */
    public void updateList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            scrollListener.resetState();
        }
    }

    /**
     * notify the recylerview a new item has been added so it can be drawn.
     */
    public void itemAdded() {
        if (adapter != null) {
            adapter.notifyItemInserted(films.size());
        }
    }

    /**
     * set the content loader to load more data when the user has reached the bottom of the page
     * @param contentLoader the content loader
     */
    public void setContentLoader(TVListFragment.ContentLoader contentLoader) {
        this.contentLoader = contentLoader;
    }
}

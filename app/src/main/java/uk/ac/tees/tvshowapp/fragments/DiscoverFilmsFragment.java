package uk.ac.tees.tvshowapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.viewmodels.FilterOptionsViewModel;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.DiscoverFilmsFilterOptions;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;

public class DiscoverFilmsFragment extends Fragment {
    private List<Film> films = new ArrayList<>();
    private FilterOptionsViewModel viewModel;
    private FilmListFragment fragment;
    private int numPages = Integer.MAX_VALUE;
    private int currentPage = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FilterOptionsViewModel.class);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_menu, menu);

        final MenuItem filterButton = menu.findItem(R.id.filter_button);
        filterButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NavDirections action = DiscoverFilmsFragmentDirections.actionDiscoverFilmsFragmentToDiscoverFilmsFilteringFragment();
                Navigation.findNavController(getView()).navigate(action);
                return true;
            }
        });

        viewModel.getFilmFilterOptions().observe(getViewLifecycleOwner(), new Observer<DiscoverFilmsFilterOptions>() {
            @Override
            public void onChanged(DiscoverFilmsFilterOptions discoverFilterOptions) {
                getDiscoverCall(discoverFilterOptions, 1).enqueue(new Callback<SearchResults<Film>>() {
                    @Override
                    public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                        if (response.isSuccessful()) {
                            currentPage++;
                            numPages = Integer.MAX_VALUE;
                            films.clear();
                            films.addAll(response.body().getResults());
                            if (fragment != null) {
                                fragment.updateList();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResults<Film>> call, Throwable t) {
                        Snackbar.make(getView(), "Failed to load, check your network", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment oldFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (oldFragment == null) {
            fragment = FilmListFragment.newInstance(films);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

        TVListFragment.ContentLoader contentLoader = new TVListFragment.ContentLoader() {
            @Override
            public void onLoadMore(int p, final int totalItemsCount, final RecyclerView view) {
                // no more pages to load
                if(currentPage > numPages){
                    return;
                }

                getDiscoverCall(viewModel.getFilmFilterOptions().getValue(), currentPage).enqueue(new Callback<SearchResults<Film>>() {
                    @Override
                    public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                        if (response.isSuccessful()) {
                            currentPage++;
                            List<Film> newItems = response.body().getResults();
                            films.addAll(newItems);
                            view.getAdapter().notifyItemRangeInserted(totalItemsCount, newItems.size());
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResults<Film>> call, Throwable t) {
                        Snackbar.make(view, "Failed to load more, check your network", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        };
        fragment.setContentLoader(contentLoader);

        return view;
    }

    private Call<SearchResults<Film>> getDiscoverCall(DiscoverFilmsFilterOptions options, int page) {
        return TmdbApi.get().getFilmService().getDiscover(options.getSortString(),
                options.getStartDateString(),
                options.getEndDateString(),
                options.getMinVoteAverage(),
                options.getMinVotes(),
                options.getGenresString(),
                page);
    }
}

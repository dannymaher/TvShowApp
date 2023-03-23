package uk.ac.tees.tvshowapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;


public class TrendingFilmFragment extends Fragment {
    private List<Film> trending = new ArrayList<>();
    private FilmListFragment fragment;
    private int currentPage = 1;
    private int numPages = Integer.MAX_VALUE;

    public TrendingFilmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        TmdbApi.get().getFilmService().getTrending(1).enqueue(new Callback<SearchResults<Film>>() {
            @Override
            public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                if (response.isSuccessful()) {
                    currentPage++;
                    trending.addAll(response.body().getResults());
                    if (fragment != null) {
                        fragment.updateList();
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResults<Film>> call, Throwable t) {
                Snackbar.make(getView(), "Failed to load trending, check your network", Snackbar.LENGTH_LONG).show();
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
            fragment = FilmListFragment.newInstance(trending);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

        TVListFragment.ContentLoader contentLoader = new TVListFragment.ContentLoader() {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, final RecyclerView view) {
                // no more pages to load
                if (currentPage > numPages) {
                    return;
                }

                TmdbApi.get().getFilmService().getTrending(currentPage).enqueue(new Callback<SearchResults<Film>>() {
                    @Override
                    public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                        if (response.isSuccessful()) {
                            currentPage++;
                            numPages = response.body().getTotalPages();
                            List<Film> newItems = response.body().getResults();
                            trending.addAll(newItems);
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
}

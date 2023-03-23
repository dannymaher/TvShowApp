package uk.ac.tees.tvshowapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public class TVSearchFragment extends Fragment {
    private List<TVShow> shows = new ArrayList<>();
    private TVListFragment fragment;
    private int currentPage = 1;
    private int numPages = Integer.MAX_VALUE;
    private String searchQuery ;

    public TVSearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TmdbApi.get().getTVService().searchTv(query,1).enqueue(new Callback<SearchResults<TVShow>>() {
                    @Override
                    public void onResponse(Call<SearchResults<TVShow>> call, Response<SearchResults<TVShow>> response) {
                        currentPage = 2;
                        shows.clear();
                        shows.addAll(response.body().getResults());
                        searchQuery = query;
                        fragment.updateList();
                    }

                    @Override
                    public void onFailure(Call<SearchResults<TVShow>> call, Throwable t) {

                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment oldFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(oldFragment ==  null){
            fragment = TVListFragment.newInstance(shows);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
        TVListFragment.ContentLoader contentLoader = new TVListFragment.ContentLoader() {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView) {
                if(currentPage > numPages){
                    return;
                }
                TmdbApi.get().getTVService().searchTv(searchQuery,currentPage).enqueue(new Callback<SearchResults<TVShow>>() {
                    @Override
                    public void onResponse(Call<SearchResults<TVShow>> call, Response<SearchResults<TVShow>> response) {
                        currentPage++;
                        numPages = response.body().getTotalPages();
                        List<TVShow> newItems = new ArrayList<>();
                        newItems = response.body().getResults();
                        shows.addAll(newItems);
                        recyclerView.getAdapter().notifyItemRangeInserted(totalItemsCount, newItems.size());
                    }

                    @Override
                    public void onFailure(Call<SearchResults<TVShow>> call, Throwable t) {

                    }
                });
            }
        };
        fragment.setContentLoader(contentLoader);
        return view;
    }
}

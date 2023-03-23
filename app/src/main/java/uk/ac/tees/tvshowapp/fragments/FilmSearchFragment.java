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
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;

public class FilmSearchFragment extends Fragment {
    private List<Film> films = new ArrayList<>();
    private FilmListFragment fragment;
    private int currentPage = 1;
    private int numPages = Integer.MAX_VALUE;
    private String searchQuery ;

    public FilmSearchFragment() {
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
                TmdbApi.get().getFilmService().searchFilm(query,1).enqueue(new Callback<SearchResults<Film>>() {
                    @Override
                    public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                        currentPage=2;
                        films.clear();
                        films.addAll(response.body().getResults());
                        searchQuery = query;
                        fragment.updateList();
                    }

                    @Override
                    public void onFailure(Call<SearchResults<Film>> call, Throwable t) {

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
            fragment = FilmListFragment.newInstance(films);
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
                TmdbApi.get().getFilmService().searchFilm(searchQuery,currentPage).enqueue(new Callback<SearchResults<Film>>() {
                    @Override
                    public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                        currentPage++;
                        numPages = response.body().getTotalPages();
                        List<Film> newItems = new ArrayList<>();
                        newItems = response.body().getResults();
                        films.addAll(newItems);
                        recyclerView.getAdapter().notifyItemRangeInserted(totalItemsCount, newItems.size());
                    }

                    @Override
                    public void onFailure(Call<SearchResults<Film>> call, Throwable t) {

                    }
                });
            }
        };

        fragment.setContentLoader(contentLoader);
        return view;



    }
}

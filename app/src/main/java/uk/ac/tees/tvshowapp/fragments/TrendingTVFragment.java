package uk.ac.tees.tvshowapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public class TrendingTVFragment extends Fragment {

    private List<TVShow> trending = new ArrayList<>();
    private TVListFragment fragment;
    private int numPages = Integer.MAX_VALUE;
    private int currentPage = 1;

    public TrendingTVFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        TmdbApi.get().getTVService().getTrending(1).enqueue(new Callback<SearchResults<TVShow>>() {
            @Override
            public void onResponse(Call<SearchResults<TVShow>> call, Response<SearchResults<TVShow>> response) {
                if (response.isSuccessful()) {
                    currentPage++;
                    trending.addAll(response.body().getResults());
                    if (fragment != null) {
                        fragment.updateList();
                    }
                }else{
                    Snackbar.make(getView(), "Failed to load trending, the database may be down.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResults<TVShow>> call, Throwable t) {
                //Display Network error message etc.
                Snackbar.make(getView(), "Failed to load trending, check your network.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        FragmentManager fragmentManager = getChildFragmentManager();
        TVListFragment oldFragment = (TVListFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        if (oldFragment == null) {
            fragment = TVListFragment.newInstance(trending);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }else{
            fragment = oldFragment;
        }

        TVListFragment.ContentLoader contentLoader = new TVListFragment.ContentLoader() {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, final RecyclerView recyclerView) {
                if (currentPage > numPages) {
                    return;
                }

                TmdbApi.get().getTVService().getTrending(currentPage).enqueue(new Callback<SearchResults<TVShow>>() {
                    @Override
                    public void onResponse(Call<SearchResults<TVShow>> call, Response<SearchResults<TVShow>> response) {
                        if (response.isSuccessful()) {
                            currentPage++;
                            numPages = response.body().getTotalPages();
                            List<TVShow> newItems = response.body().getResults();
                            trending.addAll(newItems);
                            recyclerView.getAdapter().notifyItemRangeInserted(totalItemsCount, newItems.size());
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResults<TVShow>> call, Throwable t) {
                        Snackbar.make(view, "Failed to load more, check your network.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        };
        fragment.setContentLoader(contentLoader);

        return view;
    }
}

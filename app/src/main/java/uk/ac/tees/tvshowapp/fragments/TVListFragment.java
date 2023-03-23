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
import uk.ac.tees.tvshowapp.adapters.TVListAdapter;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;
import uk.ac.tees.tvshowapp.util.NavigationUtil;

import static uk.ac.tees.tvshowapp.fragments.TVShowDetailsFragment.ARG_TVSHOW;

/**
 * for displaying a grid of tv shows
 */
public class TVListFragment extends Fragment {
    private static final String ARG_TVSHOWS = "tvShowList";

    private List<TVShow> tvShows;
    private TVListAdapter adapter;
    private ContentLoader contentLoader;
    private EndlessRecyclerViewScrollListener scrollListener;

    public TVListFragment() {
        // Required empty public constructor
    }

    public static TVListFragment newInstance(List<TVShow> tvShows) {
        TVListFragment fragment = new TVListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TVSHOWS, (Serializable) tvShows);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tvShows = (List<TVShow>) getArguments().getSerializable(ARG_TVSHOWS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_tvlist, container, false);

        RecyclerView tvListView = view.findViewById(R.id.tvList);
        tvListView.setHasFixedSize(true);

        GridLayoutManager tvListLayoutManager;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            tvListLayoutManager = new GridLayoutManager(getContext(),2);
            tvListView.addItemDecoration(new GridSpacingItemDecoration(2,24,true));
        } else {
            tvListLayoutManager = new GridLayoutManager(getContext(),4);
            tvListView.addItemDecoration(new GridSpacingItemDecoration(4,12,true));
        }
        tvListView.setLayoutManager(tvListLayoutManager);

        RecyclerView.Adapter tvListAdapter = new TVListAdapter(tvShows, tvShow -> NavigationUtil.navigateToShowDetails(view, tvShow), false);
        tvListView.setAdapter(tvListAdapter);
        adapter =  (TVListAdapter) tvListAdapter;

        scrollListener = new EndlessRecyclerViewScrollListener(tvListLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(contentLoader != null){
                    contentLoader.onLoadMore(page, totalItemsCount, view);
                }
            }
        };
        tvListView.addOnScrollListener(scrollListener);

        return view;
    }

    /**
     * re-draw the recylerview, call this when the data has been completely changed
     */
    public void updateList(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
            scrollListener.resetState();
        }
    }

    /**
     * notify the recylerview a new item has been added so it can be drawn.
     */
    public void itemAdded(){
        if(adapter != null){
            adapter.notifyItemInserted(tvShows.size());
        }
    }

    /**
     * set the content loader to load more data when the user has reached the bottom of the page
     * @param contentLoader the content loader
     */
    public void setContentLoader(ContentLoader contentLoader){
        this.contentLoader = contentLoader;
    }

    /**
     * used to load more data into the recylerview when the user has reached the end of the page
     */
    public interface ContentLoader{
        void onLoadMore(int page, final int totalItemsCount, final RecyclerView recyclerView);
    }
}

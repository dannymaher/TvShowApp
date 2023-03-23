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

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.adapters.EndlessRecyclerViewScrollListener;
import uk.ac.tees.tvshowapp.adapters.GridSpacingItemDecoration;
import uk.ac.tees.tvshowapp.adapters.PersonListAdapter;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.util.NavigationUtil;


public class PersonListFragment extends Fragment {
    private static final String ARG_PERSON = "person";
    private PersonListAdapter adapter;
    private List<Person> people;
    private TVListFragment.ContentLoader contentLoader;
    private EndlessRecyclerViewScrollListener scrollListener;

    public PersonListFragment() {
    }

    public static PersonListFragment newInstance(List<Person> people) {
        PersonListFragment fragment = new PersonListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PERSON, (Serializable) people);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            people = (List<Person>) getArguments().getSerializable(ARG_PERSON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filmlist, container, false);

        RecyclerView PersonListView = view.findViewById(R.id.filmList);
        PersonListView.setHasFixedSize(true);

        GridLayoutManager PersonListLayoutManager;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            PersonListLayoutManager = new GridLayoutManager(getContext(), 2);
            PersonListView.addItemDecoration(new GridSpacingItemDecoration(2, 24, true));
        } else {
            PersonListLayoutManager = new GridLayoutManager(getContext(), 4);
            PersonListView.addItemDecoration(new GridSpacingItemDecoration(4, 12, true));
        }
        PersonListView.setLayoutManager(PersonListLayoutManager);

        RecyclerView.Adapter PersonListAdapter = new PersonListAdapter(people, person -> NavigationUtil.navigateToPersonDetails(view, person));
        PersonListView.setAdapter(PersonListAdapter);
        adapter = (PersonListAdapter) PersonListAdapter;
        scrollListener = new EndlessRecyclerViewScrollListener(PersonListLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (contentLoader != null) {
                    contentLoader.onLoadMore(page, totalItemsCount, view);
                }
            }
        };
        PersonListView.addOnScrollListener(scrollListener);


        return view;
    }

    public void updateList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            scrollListener.resetState();
        }
    }

    public void itemAdded() {
        if (adapter != null) {
            adapter.notifyItemInserted(people.size());
        }
    }

    public void setContentLoader(TVListFragment.ContentLoader contentLoader) {
        this.contentLoader = contentLoader;
    }
}

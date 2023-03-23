package uk.ac.tees.tvshowapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.firebase.FirebaseApi;
import uk.ac.tees.tvshowapp.firebase.OnMyLibraryUpdatedListener;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

/**
 * Fragment for the My Shows section of the app
 */
public class MyShowsFragment extends Fragment {

    /**
     * List of TV Shows to be displayed
     */
    public List<TVShow> myShows;

    /**
     * The TVListFragment that will be modified to display My Shows
     */
    public TVListFragment fragment;

    public MyShowsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myShows = FirebaseApi.get(getContext()).getTrackedShows(new OnMyLibraryUpdatedListener<TVShow>() {
            @Override
            public void itemAdded(TVShow tvShow) {
                if (fragment != null) {
                    fragment.itemAdded();
                }
            }

            @Override
            public void loadComplete() {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_shows, container, false);

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment oldFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (oldFragment == null) {
            final TVListFragment fragment = TVListFragment.newInstance(myShows);
            this.fragment = fragment;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

        return view;
    }
}

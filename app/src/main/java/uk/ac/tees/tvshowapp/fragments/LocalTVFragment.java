package uk.ac.tees.tvshowapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.GeoPoint;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.MediaRepository;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.firebase.FirebaseApi;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public class LocalTVFragment extends Fragment {
    private static final String TAG = "LocalTV";

    private FirebaseApi firebaseApi;
    private String location;

    private MutableLiveData<Boolean> noData = new MutableLiveData<>();

    private List<TVShow> popularNearby = new ArrayList<>();
    private TVListFragment fragment;
    private int currentPage = 1;

    private View progressBar;

    private MutableLiveData<List<Integer>> popularNearbyIds = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LocalTVFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        firebaseApi = FirebaseApi.get(getContext());
        location = firebaseApi.getLocation();
        noData.setValue(false);

        if(location != null){
            loadPopularNeabyIds();
        }

        popularNearbyIds.observe(this, ids -> {
            if(ids.size() == 0 && popularNearby.size() == 0){
                noData.setValue(true);
                return;
            }

            for(Integer id: ids){
                Disposable disposable = MediaRepository.get(getContext()).getTVShow(id).subscribe(tvShow -> {
                    popularNearby.add(tvShow);
                    if(fragment != null){
                        fragment.itemAdded();
                    }
                    if(progressBar != null){
                        progressBar.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    Log.d(TAG, "Failed to get tvShow");
                });
                compositeDisposable.add(disposable);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_tv, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        View errorMessageContainer = view.findViewById(R.id.error_message_container);
        errorMessageContainer.setVisibility(View.GONE);

        if(location != null){
            FragmentManager fragmentManager = getChildFragmentManager();
            TVListFragment oldFragment = (TVListFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            if (oldFragment == null) {
                fragment = TVListFragment.newInstance(popularNearby);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }else{
                fragment = oldFragment;
                progressBar.setVisibility(View.GONE);
            }

            TVListFragment.ContentLoader contentLoader = (page, totalItemsCount, recyclerView) -> loadPopularNeabyIds();
            fragment.setContentLoader(contentLoader);
        }else{
            errorMessageContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        // Show an error message is there is no shows popular nearby
        noData.observe(this, nodata -> {
            if(nodata){
                TextView errorTitle = view.findViewById(R.id.error_title);
                errorTitle.setText(R.string.no_results);
                TextView errorMessage = view.findViewById(R.id.error_message);
                errorMessage.setText(R.string.no_popular_nearby_shows_message);
                errorMessageContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void loadPopularNeabyIds(){
        Log.d(TAG, "loading more ids");
        firebaseApi.getLocationService().getPopularNearbyTvIds(location, currentPage).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if(response.isSuccessful()){
                    popularNearbyIds.setValue(response.body());
                }else{
                    Snackbar.make(getView(), getString(R.string.failed_to_load_popular), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Snackbar.make(getView(), getString(R.string.failed_to_load_popular_network), Snackbar.LENGTH_LONG).show();
            }
        });
        currentPage++;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}

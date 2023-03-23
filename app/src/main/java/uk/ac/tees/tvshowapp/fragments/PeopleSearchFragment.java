package uk.ac.tees.tvshowapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;

import static uk.ac.tees.tvshowapp.fragments.PersonDetailsFragment.ARG_PERSON;

public class PeopleSearchFragment extends Fragment {
    private List<Person> people = new ArrayList<>();
    private PersonListFragment fragment;
    private int currentPage = 1;
    private int numPages = Integer.MAX_VALUE;
    private String searchQuery ;
    public PeopleSearchFragment() {
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
                System.out.println(query);
                TmdbApi.get().getPersonService().searchPeople(query,1).enqueue(new Callback<SearchResults<Person>>() {
                    @Override
                    public void onResponse(Call<SearchResults<Person>> call, Response<SearchResults<Person>> response) {
                        currentPage = 2;

                        List<Person> tempArray = new ArrayList<>();
                        for(Person person : response.body().getResults()){
                            if(person.getKnownForDepartment().equals("Acting") || person.getKnownForDepartment().equals("Directing")){
                                tempArray.add(person);
                            }
                        }

                        people.clear();
                        people.addAll(tempArray);
                        searchQuery =query;
                        fragment.updateList();
                    }

                    @Override
                    public void onFailure(Call<SearchResults<Person>> call, Throwable t) {

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
            fragment = PersonListFragment.newInstance(people);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }

        TVListFragment.ContentLoader contentLoader = new TVListFragment.ContentLoader() {
            @Override
            public void onLoadMore(int page, int totalItemsCount,final RecyclerView recyclerView) {
                if(currentPage > numPages){
                    return;
                }
               TmdbApi.get().getPersonService().searchPeople(searchQuery,currentPage).enqueue(new Callback<SearchResults<Person>>() {
                   @Override
                   public void onResponse(Call<SearchResults<Person>> call, Response<SearchResults<Person>> response) {
                       currentPage++;
                       numPages = response.body().getTotalPages();
                       List<Person> newItems = new ArrayList<>();
                       for(Person person : response.body().getResults()){
                           if(person.getKnownForDepartment().equals("Acting") || person.getKnownForDepartment().equals("Directing")){
                               newItems.add(person);
                           }
                       }
                       people.addAll(newItems);
                       recyclerView.getAdapter().notifyItemRangeInserted(totalItemsCount, newItems.size());
                   }

                   @Override
                   public void onFailure(Call<SearchResults<Person>> call, Throwable t) {

                   }
               });
            }

        };
        fragment.setContentLoader(contentLoader);
        return view;
    };

}

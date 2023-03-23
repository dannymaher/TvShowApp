package uk.ac.tees.tvshowapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.viewmodels.FilterOptionsViewModel;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.tmdb.TVService;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.DiscoverFilterOptions;
import uk.ac.tees.tvshowapp.tmdb.model.Genre;

public class DiscoverTVFilteringFragment extends Fragment {

    private MutableLiveData<List<Genre>> genres = new MutableLiveData();
    private ArrayList<CheckBox> genreCheckboxes;
    private ArrayList<String> years;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        genres.setValue(new ArrayList<Genre>());

        TmdbApi.get().getTVService().getGenres().enqueue(new Callback<TVService.GenresContainer>() {
            @Override
            public void onResponse(Call<TVService.GenresContainer> call, Response<TVService.GenresContainer> response) {
                if (response.isSuccessful()) {
                    genres.setValue(response.body().genres);
                }
            }

            @Override
            public void onFailure(Call<TVService.GenresContainer> call, Throwable t) {
                // TODO: handle failure
            }
        });

        years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add("Any");
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_options_menu, menu);

        menu.findItem(R.id.apply_filters).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                saveOptions();
                ((AppCompatActivity) getActivity()).onSupportNavigateUp();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FilterOptionsViewModel viewModel = new ViewModelProvider(requireActivity()).get(FilterOptionsViewModel.class);
        final DiscoverFilterOptions filterOptions = viewModel.getTvFilterOptions().getValue();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_filtering, container, false);

        // sort by
        Spinner sortBySpinner = view.findViewById(R.id.sort_by_spinner);
        ArrayAdapter sortSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, DiscoverFilterOptions.SortOption.values());
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortSpinnerAdapter);
        if (filterOptions.getSortBy() != null) {
            sortBySpinner.setSelection(filterOptions.getSortBy().ordinal());
        }

        // sort by asc or desc
        ArrayList<String> sortOptions =  new ArrayList<String>();
        sortOptions.add("DESC");
        sortOptions.add("ASC");
        Spinner sortByOrderSpinner = view.findViewById(R.id.sort_by_spinner_order);
        ArrayAdapter sortSpinnerOrderAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, sortOptions);
        sortSpinnerOrderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortByOrderSpinner.setAdapter(sortSpinnerOrderAdapter);
        if(!filterOptions.isSortDescending()){
            sortByOrderSpinner.setSelection(1);
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

        // start date
        Spinner startDateSpinner = view.findViewById(R.id.start_date_spinner);
        SpinnerAdapter startDateAdapter = getYearSpinnerAdapter();
        startDateSpinner.setAdapter(startDateAdapter);
        if (filterOptions.getStartDate() != null) {
            int index = years.indexOf(dateFormat.format(filterOptions.getStartDate()));
            if (index != -1) {
                startDateSpinner.setSelection(index);
            }
        }

        // end date
        Spinner endDateSpinner = view.findViewById(R.id.end_date_spinner);
        SpinnerAdapter endDateAdapter = getYearSpinnerAdapter();
        endDateSpinner.setAdapter(endDateAdapter);
        if (filterOptions.getEndDate() != null) {
            int index = years.indexOf(dateFormat.format(filterOptions.getEndDate()));
            if (index != -1) {
                endDateSpinner.setSelection(index);
            }
        }

        // min rating
        Spinner minRatingSpinner = view.findViewById(R.id.min_rating_spinner);
        ArrayList<Integer> ratings = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            ratings.add(i);
        }
        ArrayAdapter<Integer> minRatingAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, ratings);
        minRatingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minRatingSpinner.setAdapter(minRatingAdapter);
        minRatingSpinner.setSelection(ratings.indexOf((int) filterOptions.getMinVoteAverage()));

        final LinearLayout genreCheckboxContainer = view.findViewById(R.id.genre_checkbox_container);
        genres.observe(getViewLifecycleOwner(), new Observer<List<Genre>>() {
            @Override
            public void onChanged(List<Genre> genres) {
                genreCheckboxes = new ArrayList<>();
                for (Genre genre : genres) {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(genre.getName());
                    if (filterOptions.getGenreIds() != null && filterOptions.getGenreIds().contains(genre.getId())) {
                        checkBox.setChecked(true);
                    }
                    genreCheckboxes.add(checkBox);
                    genreCheckboxContainer.addView(checkBox);
                }
            }
        });


        return view;
    }

    private ArrayAdapter<String> getYearSpinnerAdapter() {
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return yearAdapter;
    }

    /**
     * Saves the filter settings
     */
    public void saveOptions() {
        DiscoverFilterOptions filterOptions = new DiscoverFilterOptions();

        // sort by
        Spinner sortBySpinner = getView().findViewById(R.id.sort_by_spinner);
        DiscoverFilterOptions.SortOption sortBy = (DiscoverFilterOptions.SortOption) sortBySpinner.getSelectedItem();
        filterOptions.setSortBy(sortBy);

        Spinner sortBySpinnerOrder = getView().findViewById(R.id.sort_by_spinner_order);
        boolean sortDesc = true;
        if(sortBySpinnerOrder.getSelectedItem().equals("ASC")){
            sortDesc = false;
        }
        filterOptions.setSortDescending(sortDesc);

        //start and end dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

        Spinner startDateSpinner = getView().findViewById(R.id.start_date_spinner);
        if (!startDateSpinner.getSelectedItem().equals("Any")) {
            try {
                Date startDate = dateFormat.parse((String) startDateSpinner.getSelectedItem());
                filterOptions.setStartDate(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Spinner endDateSpinner = getView().findViewById(R.id.end_date_spinner);
        if (!endDateSpinner.getSelectedItem().equals("Any")) {
            try {
                Date endDate = dateFormat.parse((String) endDateSpinner.getSelectedItem());
                filterOptions.setEndDate(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // min rating
        Spinner minRatingSpinner = getView().findViewById(R.id.min_rating_spinner);
        if ((int) minRatingSpinner.getSelectedItem() != 0) {
            filterOptions.setMinVoteAverage((int) minRatingSpinner.getSelectedItem());
        }

        // genres
        ArrayList<Integer> selectedGenres = new ArrayList<>();
        for (int i = 0; i < genreCheckboxes.size(); i++) {
            CheckBox checkBox = genreCheckboxes.get(i);
            if (checkBox.isChecked()) {
                selectedGenres.add(genres.getValue().get(i).getId());
            }
        }
        if (selectedGenres.size() != 0) {
            filterOptions.setGenreIds(selectedGenres);
        }else{
            filterOptions.setGenreIds(null);
        }

        FilterOptionsViewModel viewModel = new ViewModelProvider(requireActivity()).get(FilterOptionsViewModel.class);
        viewModel.getTvFilterOptions().setValue(filterOptions);
    }
}

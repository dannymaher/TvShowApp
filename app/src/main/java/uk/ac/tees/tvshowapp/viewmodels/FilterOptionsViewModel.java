package uk.ac.tees.tvshowapp.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import uk.ac.tees.tvshowapp.tmdb.model.DiscoverFilmsFilterOptions;
import uk.ac.tees.tvshowapp.tmdb.model.DiscoverFilterOptions;

public class FilterOptionsViewModel extends ViewModel {

    private MutableLiveData<DiscoverFilterOptions> tvFilterOptions = new MutableLiveData<>();
    private MutableLiveData<DiscoverFilmsFilterOptions> filmFilterOptions = new MutableLiveData<>();

    public FilterOptionsViewModel(){
        tvFilterOptions.setValue(new DiscoverFilterOptions());
        filmFilterOptions.setValue(new DiscoverFilmsFilterOptions());
    }

    public MutableLiveData<DiscoverFilterOptions> getTvFilterOptions() {
        return tvFilterOptions;
    }

    public MutableLiveData<DiscoverFilmsFilterOptions> getFilmFilterOptions() {
        return filmFilterOptions;
    }
}

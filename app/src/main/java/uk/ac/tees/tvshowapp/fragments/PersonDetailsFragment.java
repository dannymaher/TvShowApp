package uk.ac.tees.tvshowapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.adapters.FilmListAdapter;
import uk.ac.tees.tvshowapp.adapters.TVListAdapter;
import uk.ac.tees.tvshowapp.tmdb.PersonImages;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.listeners.PersonUpdateListener;
import uk.ac.tees.tvshowapp.tmdb.model.CastExtrernalId;
import uk.ac.tees.tvshowapp.tmdb.model.CastImage;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.PersonCredits;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

public class PersonDetailsFragment extends Fragment {
    public static final String ARG_PERSON = "person";
    private Person person;
    private List<CastImage> profileImage = new ArrayList<>();
    private List<CastImage> taggedImage = new ArrayList<>();
    private List<Film> films = new ArrayList<>();
    private List<TVShow> tvShows = new ArrayList<>();
    private CastExtrernalId externalId;
    private List<Film> directed = new ArrayList<>();

    public PersonDetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            person = (Person) getArguments().getSerializable(ARG_PERSON);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_person_details, container, false);
        final LinearLayout profileContainer = view.findViewById(R.id.person_image_container);

        final TextView directedHeading = view.findViewById(R.id.person_director_heading);
        final TextView filmsHeading = view.findViewById(R.id.person_films_heading);
        final TextView tvShowsHeading = view.findViewById(R.id.person_shows_heading);

        final RecyclerView directedContainer = view.findViewById(R.id.person_directed_container);
        final RecyclerView tvshowsContainer = view.findViewById(R.id.person_shows_container);
        final RecyclerView filmsContainer = view.findViewById(R.id.person_films_container);

        if (directed.size() == 0) {
            directedHeading.setVisibility(View.GONE);
        }
        if (films.size() == 0) {
            filmsHeading.setVisibility(View.GONE);
        }
        if (tvShows.size() == 0) {
            tvShowsHeading.setVisibility(View.GONE);
        }

        displayDirected(directedHeading, directedContainer);
        displayFilmsStarredIn(filmsHeading, filmsContainer);
        displayTVStarredIn(tvShowsHeading, tvshowsContainer);

        TmdbApi.get().getPersonService().getImages(person.getId()).enqueue(new Callback<PersonImages>() {
            @Override
            public void onResponse(Call<PersonImages> call, Response<PersonImages> response) {
                profileImage.clear();
                profileImage.addAll(response.body().getProfiles());
                for (CastImage profile : profileImage) {
                    View profileImage = inflater.inflate(R.layout.item_tvshow_padding, profileContainer, false);
                    ImageView image = profileImage.findViewById(R.id.show_poster);

                    if (profile.getFile() != null) {
                        Picasso.get()
                                .load(profile.getFile())
                                .placeholder(R.drawable.image_loading_full)
                                .into(image);
                    }
                    profileContainer.addView(profileImage);
                }

            }

            @Override
            public void onFailure(Call<PersonImages> call, Throwable t) {

            }
        });
        TmdbApi.get().getPersonService().getTagged(person.getId()).enqueue(new Callback<SearchResults<CastImage>>() {
            @Override
            public void onResponse(Call<SearchResults<CastImage>> call, Response<SearchResults<CastImage>> response) {
                taggedImage.clear();
                taggedImage.addAll(response.body().getResults());
                if (profileImage.size() < 10) {
                    for (int i = 0; i <= 10 & i < taggedImage.size(); i++) {
                        View profileImage = inflater.inflate(R.layout.item_tvshow_padding, profileContainer, false);
                        ImageView image = profileImage.findViewById(R.id.show_poster);
                        if (taggedImage.get(i).getFile() != null) {
                            Picasso.get()
                                    .load(taggedImage.get(i).getFile())
                                    .placeholder(R.drawable.image_loading_full)
                                    .into(image);
                        }
                        profileContainer.addView(profileImage);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResults<CastImage>> call, Throwable t) {

            }
        });

        person.ensureComplete(new PersonUpdateListener() {
            @Override
            public void onPersonUpdate() {
                TextView bio = view.findViewById(R.id.person_bio);
                bio.setText(person.getBiography());

                String deathDay;
                if (person.getDeathday() == null) {
                    deathDay = "Present";
                } else {
                    deathDay = person.getDeathday();
                }

                String gender;
                if (person.getGender() == 1) {
                    gender = "Female";
                } else {
                    gender = "Male";
                }


                TextView details = view.findViewById(R.id.person_details);
                details.setText("Name: " + person.getName() + "\nLife: (" + person.getBirthday() + " - " + deathDay + ")\nBorn In: " + person.getPlaceOfBirth() +
                        "\nGender: " + gender + "\nKnown For: " + person.getKnownForDepartment());

                TmdbApi.get().getPersonService().getExternalId(person.getId()).enqueue(new Callback<CastExtrernalId>() {
                    @Override
                    public void onResponse(Call<CastExtrernalId> call, Response<CastExtrernalId> response) {
                        externalId = response.body();
                        String facebook = "";
                        if (externalId.getFacebookId() != null && !externalId.getFacebookId().equals("null") && !externalId.getFacebookId().equals("")) {
                            facebook = "\nFacebook ID: " + externalId.getFacebookId();
                        }
                        String twitter = "";
                        if (externalId.getTwitterId() != null && !externalId.getTwitterId().equals("null")) {
                            twitter = "\nTwitter ID: " + externalId.getTwitterId();
                        }
                        String instagram = "";
                        if (externalId.getInstagramId() != null && !externalId.getInstagramId().equals("null")) {
                            instagram = "\nInstagram ID: " + externalId.getInstagramId();
                        }
                        details.append(facebook + twitter + instagram);
                    }


                    @Override
                    public void onFailure(Call<CastExtrernalId> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onPersonFailure() {

            }
        });
        return view;
    }

    // display films the person has directed
    private void displayDirected(TextView directedHeading, RecyclerView directedContainer) {
        FilmListAdapter filmListAdapter = new FilmListAdapter(directed, film -> {
            NavDirections navDirections = PersonDetailsFragmentDirections.actionGlobalFilmDetails(film.getTitle(), film);
            Navigation.findNavController(directedContainer).navigate(navDirections);
        }, true);
        directedContainer.setAdapter(filmListAdapter);

        if (directed.size() == 0) {
            if (person.getKnownForDepartment().equals("Directing")) {
                TmdbApi.get().getPersonService().getDirected(person.getId()).enqueue(new Callback<PersonCredits<Film>>() {
                    @Override
                    public void onResponse(Call<PersonCredits<Film>> call, Response<PersonCredits<Film>> response) {
                        directed.addAll(response.body().getCrew());
                        filmListAdapter.notifyItemRangeInserted(0, response.body().getCrew().size());
                        directedHeading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<PersonCredits<Film>> call, Throwable t) {

                    }
                });
            }
        }
    }

    // display tv the actor has starred in
    private void displayTVStarredIn(TextView tvShowsHeading, RecyclerView tvShowsContainer) {
        TVListAdapter tvListAdapter = new TVListAdapter(tvShows, tvShow -> {
            NavDirections navDirections = TVShowDetailsFragmentDirections.actionGlobalTVShowDetails2(tvShow, tvShow.getName());
            Navigation.findNavController(tvShowsContainer).navigate(navDirections);
        }, true);
        tvShowsContainer.setAdapter(tvListAdapter);

        if (tvShows.size() == 0) {
            TmdbApi.get().getPersonService().getShows(person.getId()).enqueue(new Callback<PersonCredits<TVShow>>() {
                @Override
                public void onResponse(Call<PersonCredits<TVShow>> call, Response<PersonCredits<TVShow>> response) {
                    if (response.isSuccessful() && response.body().getCast().size() != 0) {
                        tvShows.addAll(response.body().getCast());
                        tvListAdapter.notifyItemRangeInserted(0, response.body().getCast().size());
                        tvShowsHeading.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<PersonCredits<TVShow>> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }

    // display films the actor has starred in
    private void displayFilmsStarredIn(TextView filmsHeading, RecyclerView filmsContainer) {
        FilmListAdapter filmListAdapter = new FilmListAdapter(films, film -> {
            NavDirections navDirections = PersonDetailsFragmentDirections.actionGlobalFilmDetails(film.getTitle(), film);
            Navigation.findNavController(filmsContainer).navigate(navDirections);
        }, true);
        filmsContainer.setAdapter(filmListAdapter);

        if (films.size() == 0) {
            TmdbApi.get().getPersonService().getFilms(person.getId()).enqueue(new Callback<PersonCredits<Film>>() {
                @Override
                public void onResponse(Call<PersonCredits<Film>> call, Response<PersonCredits<Film>> response) {
                    if (response.isSuccessful() && response.body().getCast().size() != 0) {
                        films.addAll(response.body().getCast());
                        filmListAdapter.notifyItemRangeInserted(0, response.body().getCast().size());
                        filmsHeading.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<PersonCredits<Film>> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

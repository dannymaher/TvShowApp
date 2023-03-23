package uk.ac.tees.tvshowapp.fragments;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.adapters.CastListAdapter;
import uk.ac.tees.tvshowapp.adapters.CrewListAdapter;
import uk.ac.tees.tvshowapp.adapters.FilmListAdapter;
import uk.ac.tees.tvshowapp.firebase.FirebaseApi;
import uk.ac.tees.tvshowapp.firebase.OnReviewAddedListener;
import uk.ac.tees.tvshowapp.firebase.ReviewList;
import uk.ac.tees.tvshowapp.firebase.UserReview;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.listeners.FilmUpdateListener;
import uk.ac.tees.tvshowapp.tmdb.model.Cast;
import uk.ac.tees.tvshowapp.tmdb.model.Credits;
import uk.ac.tees.tvshowapp.tmdb.model.Crew;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.Genre;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.ProductionCompany;
import uk.ac.tees.tvshowapp.tmdb.model.ProductionCountry;
import uk.ac.tees.tvshowapp.tmdb.model.Review;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.SpokenLanguageFilm;
import uk.ac.tees.tvshowapp.tmdb.model.enums.BackdropImageSize;
import uk.ac.tees.tvshowapp.util.CalendarEvents;

import static uk.ac.tees.tvshowapp.fragments.ReviewDetailsFragment.ARG_REVIEW;

/**
 * A Fragment used to display the details of a specific Film
 */
public class FilmDetailsFragment extends Fragment {

    /**
     * The Name this Fragment is referred to by the app itself
     */
    public static final String ARG_FILM = "film";

    /**
     * The Film currently being displayed by the Fragment
     */
    private Film film;

    /**
     * List of similar Films
     */
    private List<Film> similar = new ArrayList<>();

    /**
     * List of recommended Films
     */
    private List<Film> recommendations = new ArrayList<>();

    /**
     * List of Cast for the Film
     */
    private List<Cast> cast = new ArrayList<>();

    /**
     * List of Directors for the Film
     */
    private List<Crew> directors = new ArrayList<>();

    /**
     * List of Reviews retrieved from the Firebase
     */
    private ArrayList<UserReview> reviews;

    /**
     * List of Reviews retrieved from tmdb
     */
    private List<Review> apiReviews = new ArrayList<>();

    /**
     * Average rating of the Film
     */
    private double avgRating;

    public FilmDetailsFragment() {
        //required empty public constructor
    }

    /**
     * Creates a new Film Fragment at App boot-up
     *
     * @param film The Film that will be displayed
     * @return The Film Fragment
     */
    public static FilmDetailsFragment newInstance(Film film) {
        FilmDetailsFragment fragment = new FilmDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILM, film);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            film = (Film) getArguments().getSerializable(ARG_FILM);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favourite_menu, menu);
        final MenuItem favButton = menu.findItem(R.id.favourite);

        FirebaseApi.get(getContext()).getTrackedFilmIds().observe(this, trackedFilmIds -> {
            if (trackedFilmIds.contains(Long.valueOf(film.getId()))) {
                favButton.setIcon(R.drawable.ic_favorite_black_24dp);
            } else {
                favButton.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }
        });

        favButton.setOnMenuItemClickListener(item -> {
            FirebaseApi firebaseApi = FirebaseApi.get(getContext());
            if (firebaseApi.getTrackedFilmIds().getValue().contains(Long.valueOf(film.getId()))) {
                firebaseApi.unsubscribeFilm(film);
            } else {
                firebaseApi.subscribeFilm(film);
            }
            return true;
        });

        // add add to calendar button
        inflater.inflate(R.menu.calendar_menu, menu);
        final MenuItem calendarButton = menu.findItem(R.id.add_to_calendar);
        calendarButton.setOnMenuItemClickListener(item -> CalendarEvents.insertFilmEvent(film, getContext()));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_film_details, container, false);
        final RecyclerView similarContainer = view.findViewById(R.id.similar_container);
        final RecyclerView recContainer = view.findViewById(R.id.rec_container);
        final RecyclerView castContainer = view.findViewById(R.id.cast_container);
        final RecyclerView crewContainer = view.findViewById(R.id.film_director_container);
        final LinearLayout reviewContainer = view.findViewById(R.id.film_review_container);

        final TextView crewHeading = view.findViewById(R.id.film_director_heading);
        final TextView castHeading = view.findViewById(R.id.cast_heading);
        final TextView recHeading = view.findViewById(R.id.rec_heading);
        final TextView similarHeading = view.findViewById(R.id.similar_heading);
        final TextView reviewHeading = view.findViewById(R.id.film_review_heading);

        if (cast.size() == 0) {
            castHeading.setVisibility(View.GONE);
        }
        if (similar.size() == 0) {
            similarHeading.setVisibility(View.GONE);
        }
        if (recommendations.size() == 0) {
            recHeading.setVisibility(View.GONE);
        }
        if (directors.size() == 0) {
            similarHeading.setVisibility(View.GONE);
        }

        displayCast(castHeading, castContainer, crewHeading, crewContainer);
        displaySimilar(similarHeading, similarContainer);
        displayRecommended(recHeading, recContainer);

        film.ensureComplete(new FilmUpdateListener() {
            @Override
            public void onFilmUpdate() {
                LinearLayout genresContainer = view.findViewById(R.id.genres_container);
                LinearLayout countryContainer = view.findViewById(R.id.country_container);
                LinearLayout companyContainer = view.findViewById(R.id.company_container);
                LinearLayout languageContainer = view.findViewById(R.id.language_container);
                populateReviews(new OnReviewAddedListener() {
                    @Override
                    public void reviewsLoaded() {
                        EditText userReview = view.findViewById(R.id.film_post_review);
                        Button postReview = view.findViewById(R.id.film_post_review_button);
                        postReview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast toast;
                                String content = userReview.getText().toString();
                                if (!content.equals("")) {
                                    FirebaseApi api = FirebaseApi.get(getContext());
                                    //TODO: Implement ratings, currently just sets the rating to 0 as no way to set it
                                    api.postFilmReview(new Long(0), content, film.getId().longValue());
                                    toast = Toast.makeText(getContext(), "Review has been posted", Toast.LENGTH_SHORT);
                                    userReview.setText("");
                                } else {
                                    toast = Toast.makeText(getContext(), "Cannot post blank Review", Toast.LENGTH_SHORT);
                                }
                                toast.show();
                            }
                        });
                        //add code to add firebase reviews also
                        if (apiReviews.size() == 0) {
                            TmdbApi.get().getFilmService().getReviews(film.getId()).enqueue(new Callback<SearchResults<Review>>() {
                                @Override
                                public void onResponse(Call<SearchResults<Review>> call, Response<SearchResults<Review>> response) {
                                    if (response.isSuccessful() && response.body().getTotalResults() != 0) {
                                        if (reviews != null) {
                                            for (UserReview r : reviews) {
                                                Review tempReview = new Review();
                                                tempReview.setContent(r.getReviewText());
                                                apiReviews.add(tempReview);
                                            }
                                        }
                                        apiReviews.addAll(response.body().getResults());
                                        //TODO: This loop may need to be changed


                                        for (Review review : apiReviews) {
                                            View reviewView = inflater.inflate(R.layout.item_review, reviewContainer, false);
                                            TextView reviewText = reviewView.findViewById(R.id.review_text);

                                            reviewText.setText(review.getContent());

                                            reviewView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(ARG_REVIEW, review);
                                                    bundle.putSerializable("title", film.getTitle());

                                                    NavOptions navOptions = new NavOptions.Builder()
                                                            .setEnterAnim(R.anim.fragment_open_enter)
                                                            .setExitAnim(R.anim.fragment_open_exit)
                                                            .setPopEnterAnim(R.anim.fragment_close_enter)
                                                            .setPopExitAnim(R.anim.fragment_close_exit)
                                                            .build();

                                                    Navigation.findNavController(view).navigate(R.id.reviewDetailsFragment, bundle, navOptions);
                                                }
                                            });
                                            reviewContainer.addView(reviewView);
                                        }
                                    } else {
                                        // TODO: handle failure;
                                    }
                                }

                                @Override
                                public void onFailure(Call<SearchResults<Review>> call, Throwable t) {
                                    // TODO: handle failure;
                                }
                            });
                        } else {

                            for (Review review : apiReviews) {
                                View reviewView = inflater.inflate(R.layout.item_review, reviewContainer, false);
                                TextView reviewText = reviewView.findViewById(R.id.review_text);

                                reviewText.setText(review.getContent());
                                reviewView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(ARG_REVIEW, review);
                                        bundle.putSerializable("title", film.getTitle());

                                        NavOptions navOptions = new NavOptions.Builder()
                                                .setEnterAnim(R.anim.fragment_open_enter)
                                                .setExitAnim(R.anim.fragment_open_exit)
                                                .setPopEnterAnim(R.anim.fragment_close_enter)
                                                .setPopExitAnim(R.anim.fragment_close_exit)
                                                .build();

                                        Navigation.findNavController(view).navigate(R.id.reviewDetailsFragment, bundle, navOptions);
                                    }
                                });
                                reviewContainer.addView(reviewView);
                            }
                        }
                    }
                });


                for (Genre genre : film.getGenres()) {
                    View genreLabel = inflater.inflate(R.layout.genre_label, genresContainer, false);
                    TextView genreName = genreLabel.findViewById(R.id.genre_name);
                    genreName.setText(genre.getName());
                    genresContainer.addView(genreLabel);
                }

                for (ProductionCountry country : film.getProductionCountries()) {
                    View countryLabel = inflater.inflate(R.layout.generic_label, countryContainer, false);
                    TextView countryName = countryLabel.findViewById(R.id.genre_name);
                    countryName.setText(country.getName());
                    countryContainer.addView(countryLabel);
                }

                for (ProductionCompany company : film.getProductionCompanies()) {
                    View companyLabel = inflater.inflate(R.layout.generic_label, companyContainer, false);
                    TextView companyName = companyLabel.findViewById(R.id.genre_name);
                    companyName.setText(company.getName());
                    companyContainer.addView(companyLabel);
                }

                for (SpokenLanguageFilm language : film.getSpokenLanguages()) {
                    View languageLabel = inflater.inflate(R.layout.generic_label, languageContainer, false);
                    TextView languageName = languageLabel.findViewById(R.id.genre_name);
                    languageName.setText(language.getInitial() + " - " + language.getName());
                    languageContainer.addView(languageLabel);
                }


                TextView filmRuntime = view.findViewById(R.id.film_runtime);
                filmRuntime.setText(film.getRuntime() + " minutes");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view, "Failed to load film details, check your network.", Snackbar.LENGTH_LONG).show();
            }
        });


        TextView filmName = view.findViewById(R.id.film_name);
        filmName.setText(film.getTitle());
        TextView filmDate = view.findViewById(R.id.film_dates);
        filmDate.setText(film.getReleaseDateFormatted());
        TextView filmRuntime = view.findViewById(R.id.film_runtime);
        filmRuntime.setText(film.getRuntime() + " minutes");
        TextView filmOverview = view.findViewById(R.id.film_overview);
        filmOverview.setText(film.getOverview());
        ImageView filmImage = view.findViewById(R.id.film_backdrop);
        Picasso.get()
                .load(film.getBackdropPath(BackdropImageSize.w1280))
                .into(filmImage);
        return view;
    }

    /**
     * Gets and displays the cast and crew of the film
     *
     * @param castHeading       Heading text for cast
     * @param castShowContainer Container for the Cast Member image
     * @param crewHeading       Heading text for crew
     * @param crewContainer     Container for the Crew Member image
     */
    private void displayCast(TextView castHeading, RecyclerView castShowContainer, TextView crewHeading, RecyclerView crewContainer) {
        CastListAdapter castListAdapter = new CastListAdapter(cast, cast -> TmdbApi.get().getPersonService().getPerson(cast.getId()).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Person person = response.body();
                    NavDirections navDirections = FilmDetailsFragmentDirections.actionFilmDetailsToPersonDetails(person.getName(), person);
                    Navigation.findNavController(castShowContainer).navigate(navDirections);
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                // TODO: handle failure;
            }
        }));
        castShowContainer.setAdapter(castListAdapter);

        if (cast.size() == 0) {
            TmdbApi.get().getFilmService().getCast(film.getId()).enqueue(new Callback<Credits>() {
                @Override
                public void onResponse(Call<Credits> call, Response<Credits> response) {
                    if (response.isSuccessful()) {
                        cast.addAll(response.body().getCast());
                        castListAdapter.notifyItemRangeInserted(0, response.body().getCast().size());
                        castHeading.setVisibility(View.VISIBLE);

                        displayDirector(crewHeading, crewContainer, response.body().getCrew());
                    }
                }

                @Override
                public void onFailure(Call<Credits> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }


    /**
     * Gets and displays a list of similar Films
     *
     * @param similarHeading       Heading text
     * @param similarShowContainer Container for the Film image
     */
    private void displaySimilar(TextView similarHeading, RecyclerView similarShowContainer) {
        FilmListAdapter filmListAdapter = new FilmListAdapter(similar, film1 -> {
            NavDirections navDirections = FilmDetailsFragmentDirections.actionGlobalFilmDetails(film1.getTitle(), film1);
            Navigation.findNavController(similarShowContainer).navigate(navDirections);
        }, true);
        similarShowContainer.setAdapter(filmListAdapter);

        if (similar.size() == 0) {
            TmdbApi.get().getFilmService().getSimilar(film.getId()).enqueue(new Callback<SearchResults<Film>>() {
                @Override
                public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                    if (response.isSuccessful() && response.body().getTotalResults() != 0) {
                        similar.addAll(response.body().getResults());
                        filmListAdapter.notifyItemRangeInserted(0, response.body().getResults().size());
                        similarHeading.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<SearchResults<Film>> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }


    /**
     * Gets and displays a list of Film recommendations
     *
     * @param recHeading       Heading text
     * @param recShowContainer Container for the Film image
     */
    private void displayRecommended(TextView recHeading, RecyclerView recShowContainer) {
        FilmListAdapter filmListAdapter = new FilmListAdapter(recommendations, film1 -> {
            NavDirections navDirections = FilmDetailsFragmentDirections.actionGlobalFilmDetails(film1.getTitle(), film1);
            Navigation.findNavController(recShowContainer).navigate(navDirections);
        }, true);
        recShowContainer.setAdapter(filmListAdapter);

        if (recommendations.size() == 0) {
            TmdbApi.get().getFilmService().getRecommedations(film.getId()).enqueue(new Callback<SearchResults<Film>>() {
                @Override
                public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                    if (response.isSuccessful() && response.body().getTotalResults() != 0) {
                        recommendations.addAll(response.body().getResults());
                        filmListAdapter.notifyItemRangeInserted(0, response.body().getResults().size());
                        recHeading.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<SearchResults<Film>> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }

    /**
     * Gets and displays a list of directors for the Film
     *
     * @param directorsHeading   Heading text
     * @param directorsContainer Container for the image of the director
     * @param crew               All Crew for the film
     */
    private void displayDirector(TextView directorsHeading, RecyclerView directorsContainer, List<Crew> crew) {
        CrewListAdapter crewListAdapter = new CrewListAdapter(directors, crewMember -> TmdbApi.get().getPersonService().getPerson(crewMember.getId()).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Person director = response.body();
                    NavDirections navDirections = FilmDetailsFragmentDirections.actionFilmDetailsToPersonDetails(director.getName(), director);
                    Navigation.findNavController(directorsContainer).navigate(navDirections);
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                // TODO: handle failure;
            }
        }));
        directorsContainer.setAdapter(crewListAdapter);

        for (Crew crewMember : crew) {
            if (crewMember.getJob().equals("Director") || crewMember.getDepartment().equals("Directing")) {
                directorsHeading.setVisibility(View.VISIBLE);
                directors.add(crewMember);
                int itemCount = crewListAdapter.getItemCount();
                crewListAdapter.notifyItemRangeInserted(itemCount, itemCount + 1);
            }
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

    /**
     * Populates the Average Rating and Reviews of the class
     */
    public void populateReviews(final OnReviewAddedListener popListener) {
        FirebaseApi api = FirebaseApi.get(getContext());
        api.getReviewsFor(film.getId().longValue(), "MovieReviews", new OnReviewAddedListener() {
            @Override
            public void reviewsLoaded() {
                ReviewList reviewList = api.reviewList;
                avgRating = reviewList.getAverageRating();
                reviews = reviewList.getReviews();
                System.out.println(reviews.isEmpty());
                popListener.reviewsLoaded();
            }
        });
    }
}

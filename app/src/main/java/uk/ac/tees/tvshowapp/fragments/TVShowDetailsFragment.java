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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.R;
import uk.ac.tees.tvshowapp.adapters.CastListAdapter;
import uk.ac.tees.tvshowapp.adapters.TVListAdapter;
import uk.ac.tees.tvshowapp.firebase.FirebaseApi;
import uk.ac.tees.tvshowapp.firebase.OnReviewAddedListener;
import uk.ac.tees.tvshowapp.firebase.ReviewList;
import uk.ac.tees.tvshowapp.firebase.UserReview;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.listeners.TVShowUpdateListener;
import uk.ac.tees.tvshowapp.tmdb.model.Cast;
import uk.ac.tees.tvshowapp.tmdb.model.Credits;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Genre;
import uk.ac.tees.tvshowapp.tmdb.model.Person;
import uk.ac.tees.tvshowapp.tmdb.model.Review;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.Season;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;
import uk.ac.tees.tvshowapp.tmdb.model.enums.BackdropImageSize;
import uk.ac.tees.tvshowapp.tmdb.model.enums.EpisodeImageSize;
import uk.ac.tees.tvshowapp.tmdb.model.enums.PosterImageSize;

import static uk.ac.tees.tvshowapp.fragments.ReviewDetailsFragment.ARG_REVIEW;

/**
 * A Fragment used to display the details of a specific TV Show
 */
public class TVShowDetailsFragment extends Fragment {

    /**
     * The Name this fragment is referred to by the app itself
     */
    public static final String ARG_TVSHOW = "tvShow";

    /**
     * The TV Show currently being displayed by the Fragment
     */
    private TVShow tvShow;

    /**
     * A list of similar TV Shows
     */
    private ArrayList<TVShow> similar = new ArrayList<>();

    /**
     * A list of recommended TV Shows
     */
    private ArrayList<TVShow> recommendations = new ArrayList<>();

    /**
     * A list of the shows Cast Members
     */
    private ArrayList<Cast> cast = new ArrayList<>();

    /**
     * A list of the show's Reviews from the Firebase
     */
    private ArrayList<UserReview> reviews;

    /**
     * A list of the show's Reviews from tmdb
     */
    private List<Review> apiReviews = new ArrayList<>();

    /**
     * The show's average rating
     */
    private double avgRating;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public TVShowDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new TV Show Fragment at App boot-up
     *
     * @param tvShow The TV Show that will be displayed
     * @return The TV Show Fragment
     */
    public static TVShowDetailsFragment newInstance(TVShow tvShow) {
        TVShowDetailsFragment fragment = new TVShowDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TVSHOW, tvShow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tvShow = (TVShow) getArguments().getSerializable(ARG_TVSHOW);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favourite_menu, menu);
        final MenuItem favButton = menu.findItem(R.id.favourite);

        FirebaseApi.get(getContext()).getTrackedShowIds().observe(this, trackedShowIds -> {
            if (trackedShowIds.contains(Long.valueOf(tvShow.getId()))) {
                favButton.setIcon(R.drawable.ic_favorite_black_24dp);
            } else {
                favButton.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }
        });

        favButton.setOnMenuItemClickListener(item -> {
            FirebaseApi firebaseApi = FirebaseApi.get(getContext());
            if (firebaseApi.getTrackedShowIds().getValue().contains(Long.valueOf(tvShow.getId()))) {
                firebaseApi.unsubscribeShow(tvShow);
            } else {
                firebaseApi.subscribeShow(tvShow);
            }
            return true;
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tvshow_details, container, false);
        final TextView castHeading = view.findViewById(R.id.cast_show_heading);
        final TextView similarHeading = view.findViewById(R.id.similar_heading);
        final TextView recHeading = view.findViewById(R.id.rec_show_heading);

        if (cast.size() == 0) {
            castHeading.setVisibility(View.GONE);
        }
        if (similar.size() == 0) {
            similarHeading.setVisibility(View.GONE);
        }
        if (recommendations.size() == 0) {
            recHeading.setVisibility(View.GONE);
        }


        final RecyclerView castShowContainer = view.findViewById(R.id.cast_show_container);
        final RecyclerView similarShowContainer = view.findViewById(R.id.similar_show_container);
        final RecyclerView recShowContainer = view.findViewById(R.id.rec_show_container);

        displayCast(castHeading, castShowContainer);
        displaySimilar(similarHeading, similarShowContainer);
        displayRecommended(recHeading, recShowContainer);


        // update ui when details have been fetched
        tvShow.ensureComplete(new TVShowUpdateListener() {
            @Override
            public void onTVShowUpdate() {
                TextView showDates = view.findViewById(R.id.show_dates);
                showDates.setText(tvShow.getDateString());

                // add genre labels
                LinearLayout genresContainer = view.findViewById(R.id.genres_container);
                LinearLayout reviewsContainer = view.findViewById(R.id.tv_review_container);
                populateReviews(new OnReviewAddedListener() {
                    @Override
                    public void reviewsLoaded() {
                        EditText userReview = view.findViewById(R.id.tv_post_review);
                        Button postReview = view.findViewById(R.id.tv_post_review_button);
                        postReview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast toast;
                                String content = userReview.getText().toString();
                                if (!content.equals("")) {
                                    FirebaseApi api = FirebaseApi.get(getContext());
                                    //TODO: Implement ratings, currently just sets the rating to 0 as no way to set it
                                    api.postTVReview(new Long(0), content, tvShow.getId().longValue());
                                    toast = Toast.makeText(getContext(), "Review has been posted", Toast.LENGTH_SHORT);
                                    userReview.setText("");
                                } else {
                                    toast = Toast.makeText(getContext(), "Cannot post blank Review", Toast.LENGTH_SHORT);
                                }
                                toast.show();
                            }
                        });
                        if (apiReviews.size() == 0) {
                            TmdbApi.get().getTVService().getReviews(tvShow.getId()).enqueue(new Callback<SearchResults<Review>>() {
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

                                        for (Review review : apiReviews) {
                                            View reviewView = inflater.inflate(R.layout.item_review, reviewsContainer, false);
                                            TextView reviewText = reviewView.findViewById(R.id.review_text);

                                            reviewText.setText(review.getContent());
                                            reviewView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(ARG_REVIEW, review);
                                                    bundle.putSerializable("title", tvShow.getName());

                                                    NavOptions navOptions = new NavOptions.Builder()
                                                            .setEnterAnim(R.anim.fragment_open_enter)
                                                            .setExitAnim(R.anim.fragment_open_exit)
                                                            .setPopEnterAnim(R.anim.fragment_close_enter)
                                                            .setPopExitAnim(R.anim.fragment_close_exit)
                                                            .build();

                                                    Navigation.findNavController(view).navigate(R.id.reviewDetailsFragment, bundle, navOptions);
                                                }
                                            });
                                            reviewsContainer.addView(reviewView);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<SearchResults<Review>> call, Throwable t) {

                                }
                            });
                        } else {

                            for (Review review : apiReviews) {
                                View reviewView = inflater.inflate(R.layout.item_review, reviewsContainer, false);
                                TextView reviewText = reviewView.findViewById(R.id.review_text);

                                reviewText.setText(review.getContent());
                                reviewView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(ARG_REVIEW, review);
                                        bundle.putSerializable("title", tvShow.getName());

                                        NavOptions navOptions = new NavOptions.Builder()
                                                .setEnterAnim(R.anim.fragment_open_enter)
                                                .setExitAnim(R.anim.fragment_open_exit)
                                                .setPopEnterAnim(R.anim.fragment_close_enter)
                                                .setPopExitAnim(R.anim.fragment_close_exit)
                                                .build();

                                        Navigation.findNavController(view).navigate(R.id.reviewDetailsFragment, bundle, navOptions);
                                    }
                                });
                                reviewsContainer.addView(reviewView);
                            }
                        }
                    }
                });


                for (Genre genre : tvShow.getGenres()) {
                    View genreLabel = inflater.inflate(R.layout.genre_label, genresContainer, false);
                    TextView genreName = genreLabel.findViewById(R.id.genre_name);
                    genreName.setText(genre.getName());
                    genresContainer.addView(genreLabel);
                }

                // add next / last episode details
                Episode nextEpisode = tvShow.getNextEpisodeToAir();
                if (nextEpisode == null) {
                    nextEpisode = tvShow.getLastEpisodeToAir();
                    TextView nextEpisodeText = view.findViewById(R.id.next_episode_text);
                    nextEpisodeText.setText(R.string.last_episode);
                }
                TextView nextEpisodeName = view.findViewById(R.id.episode_name);
                nextEpisodeName.setText(nextEpisode.getName());
                TextView nextEpisodeOverview = view.findViewById(R.id.episode_overview);
                nextEpisodeOverview.setText(nextEpisode.getOverview());
                ImageView nextEpisodeImage = view.findViewById(R.id.episode_image);

                if (nextEpisode.hasOverview()) {
                    CardView nextEpisodeCard = view.findViewById(R.id.episode_card);
                    final Episode finalNextEpisode = nextEpisode;
                    nextEpisodeCard.setOnClickListener(v -> {
                        NavDirections action = TVShowDetailsFragmentDirections.actionTVShowDetailsToEpisodeDetails(finalNextEpisode, tvShow.getName());
                        Navigation.findNavController(view).navigate(action);
                    });
                }

                if (nextEpisode.getStill(EpisodeImageSize.original) != null) {
                    Picasso.get()
                            .load(nextEpisode.getStill(EpisodeImageSize.w185))
                            .placeholder(R.drawable.image_loading_full)
                            .fit()
                            .centerCrop()
                            .into(nextEpisodeImage);
                } else {
                    nextEpisodeImage.setVisibility(View.GONE);
                }

                List<Season> seasons = tvShow.getSeasons();
                final Season lastSeason = seasons.get(seasons.size() - 1);

                // add last season details
                TextView lastSeasonName = view.findViewById(R.id.season_name);
                lastSeasonName.setText(lastSeason.getName());
                TextView lastSeasonOverView = view.findViewById(R.id.season_overview);
                lastSeasonOverView.setText(lastSeason.getOverview());
                ImageView lastSeasonPoster = view.findViewById(R.id.season_poster);
                CardView lastSeasonCard = view.findViewById(R.id.season_card);
                Picasso.get()
                        .load(lastSeason.getPosterPath(PosterImageSize.w342))
                        .placeholder(R.drawable.image_loading_full)
                        .fit()
                        .centerInside()
                        .into(lastSeasonPoster);

                lastSeasonCard.setOnClickListener(v -> {
                    NavDirections action = TVShowDetailsFragmentDirections.actionTVShowDetailsToSeasonDetails(lastSeason, tvShow.getId(), tvShow.getName());
                    Navigation.findNavController(view).navigate(action);
                });

                TextView allSeasonsButton = view.findViewById(R.id.all_seasons_button);
                allSeasonsButton.setOnClickListener(v -> {
                    NavDirections action = TVShowDetailsFragmentDirections.actionTVShowDetailsToSeasonListFragment(tvShow, tvShow.getName());
                    Navigation.findNavController(view).navigate(action);
                });
            }

            @Override
            public void onFailure() {
                Snackbar.make(view, "Failed to load tv show details, check your network.", Snackbar.LENGTH_LONG).show();
            }
        }, getContext(), compositeDisposable);

        TextView showName = view.findViewById(R.id.show_name);
        showName.setText(tvShow.getName());

        TextView showOverview = view.findViewById(R.id.show_overview);
        showOverview.setText(tvShow.getOverview());

        ImageView showBackdrop = view.findViewById(R.id.show_backdrop);
        Picasso.get().load(tvShow.getBackdrop(BackdropImageSize.w1280)).into(showBackdrop);

        return view;
    }

    /**
     * Gets and displays the show cast
     *
     * @param castHeading       Heading text
     * @param castShowContainer Container for the Cast member image
     */
    private void displayCast(TextView castHeading, RecyclerView castShowContainer) {
        CastListAdapter castListAdapter = new CastListAdapter(cast, cast -> TmdbApi.get().getPersonService().getPerson(cast.getId()).enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful()) {
                    Person person = response.body();
                    NavDirections navDirections = TVShowDetailsFragmentDirections.actionTVShowDetailsToPersonDetailsFragment(person.getName(), person);
                    Navigation.findNavController(castShowContainer).navigate(navDirections);
                } else {
                    // TODO: handle failure
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                // TODO: handle failure;
            }
        }));
        castShowContainer.setAdapter(castListAdapter);

        if (cast.size() == 0) {
            TmdbApi.get().getTVService().getCast(tvShow.getId()).enqueue(new Callback<Credits>() {
                @Override
                public void onResponse(Call<Credits> call, Response<Credits> response) {
                    if (response.isSuccessful() && response.body().getCast().size() != 0) {
                        cast.addAll(response.body().getCast());
                        castListAdapter.notifyItemRangeInserted(0, response.body().getCast().size());
                        castHeading.setVisibility(View.VISIBLE);
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
     * Gets and displays a list of Similar TV Shows
     *
     * @param similarHeading       Heading text
     * @param similarShowContainer Container for the show image
     */
    private void displaySimilar(TextView similarHeading, RecyclerView similarShowContainer) {
        TVListAdapter tvListAdapter = new TVListAdapter(similar, tvShow1 -> {
            NavDirections navDirections = TVShowDetailsFragmentDirections.actionGlobalTVShowDetails2(tvShow1, tvShow1.getName());
            Navigation.findNavController(similarShowContainer).navigate(navDirections);
        }, true);
        similarShowContainer.setAdapter(tvListAdapter);

        if (similar.size() == 0) {
            TmdbApi.get().getTVService().getSimilar(tvShow.getId()).enqueue(new Callback<SearchResults<TVShow>>() {
                @Override
                public void onResponse(Call<SearchResults<TVShow>> call, Response<SearchResults<TVShow>> response) {
                    if (response.isSuccessful() && response.body().getTotalResults() != 0) {
                        similar.addAll(response.body().getResults());
                        tvListAdapter.notifyItemRangeInserted(0, response.body().getResults().size());
                        similarHeading.setVisibility(View.VISIBLE);
                    } else {
                        // TODO: handle failure;
                    }
                }

                @Override
                public void onFailure(Call<SearchResults<TVShow>> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }

    /**
     * Gets and displays a list of recommended shows
     *
     * @param recHeading       Heading text
     * @param recShowContainer Container for the show image
     */
    private void displayRecommended(TextView recHeading, RecyclerView recShowContainer) {
        TVListAdapter tvListAdapter = new TVListAdapter(recommendations, tvShow1 -> {
            NavDirections navDirections = TVShowDetailsFragmentDirections.actionGlobalTVShowDetails2(tvShow1, tvShow1.getName());
            Navigation.findNavController(recShowContainer).navigate(navDirections);
        }, true);
        recShowContainer.setAdapter(tvListAdapter);

        if (recommendations.size() == 0) {
            TmdbApi.get().getTVService().getRecommedations(tvShow.getId()).enqueue(new Callback<SearchResults<TVShow>>() {
                @Override
                public void onResponse(Call<SearchResults<TVShow>> call, Response<SearchResults<TVShow>> response) {
                    if (response.isSuccessful() && response.body().getTotalResults() != 0) {
                        recommendations.addAll(response.body().getResults());
                        tvListAdapter.notifyItemRangeInserted(0, response.body().getResults().size());
                        recHeading.setVisibility(View.VISIBLE);
                    } else {
                        // TODO: handle failure;
                    }
                }

                @Override
                public void onFailure(Call<SearchResults<TVShow>> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    /**
     * Populates the show's rating and reviews
     *
     * @param popListener Listener to know when the reviews have been retrieved
     */
    public void populateReviews(final OnReviewAddedListener popListener) {
        FirebaseApi api = FirebaseApi.get(getContext());
        api.getReviewsFor(tvShow.getId().longValue(), "TvReviews", new OnReviewAddedListener() {
            @Override
            public void reviewsLoaded() {
                ReviewList reviewList = api.reviewList;
                avgRating = reviewList.getAverageRating();
                reviews = reviewList.getReviews();
                popListener.reviewsLoaded();
            }
        });
    }
}

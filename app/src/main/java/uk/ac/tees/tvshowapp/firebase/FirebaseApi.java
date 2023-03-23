package uk.ac.tees.tvshowapp.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.tees.tvshowapp.MediaRepository;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;

/**
 * An Api class used to contain any methods that require pushing or pulling data involving our
 * Firebase database, it is a singleton class
 */
public class FirebaseApi {

    /**
     * The name this class is referred to by the app
     */
    private static final String TAG = "Firebase";

    private final String PREFS_NAME = "MyLibrary";
    private final String PREF_TRACKED_SHOWS = "trackedShowIds";
    private final String PREF_TRACKED_FILMS = "trackedFilmIds";

    /**
     * The instance of FirebaseApi that can exist, as no more can be created
     */
    private static FirebaseApi instance;

    /**
     * Current list of the users tracked shows
     */
    private ArrayList<TVShow> trackedShows;

    /**
     * Current list of the users tracked films
     */
    private ArrayList<Film> trackedFilms;

    /**
     * List of Firebase Reviews for the page you're currently viewing
     */
    public ReviewList reviewList;

    /**
     * List of Tracked Show ids for the currently logged in user
     */
    private MutableLiveData<ArrayList<Long>> trackedShowIds = new MutableLiveData<>();

    /**
     * List of Tracked Film ids for the currently logged in user
     */
    private MutableLiveData<ArrayList<Long>> trackedFilmIds = new MutableLiveData<>();

    /**
     * The location service used to find the user's location
     */
    private LocationService locationService;

    /**
     * Current location of the user
     */
    private String location;

    /**
     * Private list of reviews from the Firebase
     */
    private ArrayList<UserReview> userReviews = new ArrayList<>();

    private Context context;

    /**
     * FirebaseApi constructor
     *
     * @param context The context of the app
     */
    private FirebaseApi(Context context) {
        this.context = context.getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://europe-west1-tvshowapp-631e9.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        locationService = retrofit.create(LocationService.class);

        getTrackedIds();
    }

    /**
     * Returns an instance of FirebaseApi to be called on in a method
     *
     * @param context any context
     * @return an instance of FirebaseApi
     */
    public static synchronized FirebaseApi get(Context context) {
        if (instance == null) {
            instance = new FirebaseApi(context);
        }
        return instance;
    }

    /**
     * Get the tracked film and show ids, which is cached in Firebase for instant access
     * (necessary for notifications since the service has to run synchronously)
     * Then pulls from firebase the updated list.
     */
    private void getTrackedIds() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();


        String tvShowIdListJson = sharedPreferences.getString(PREF_TRACKED_SHOWS, null);
        if (tvShowIdListJson != null) {
            trackedShowIds.postValue(gson.fromJson(tvShowIdListJson, new TypeToken<List<Long>>() {
            }.getType()));
        } else {
            trackedShowIds.postValue(new ArrayList<>());
        }

        String filmIdListJson = sharedPreferences.getString(PREF_TRACKED_FILMS, null);
        if (filmIdListJson != null) {
            trackedFilmIds.postValue(gson.fromJson(filmIdListJson, new TypeToken<List<Long>>() {
            }.getType()));
        } else {
            trackedFilmIds.postValue(new ArrayList<>());
        }


        getTrackedShowsRef().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Long> showIds = getTrackedShowIds(task.getResult());
                trackedShowIds.postValue(showIds);
                putTrackedShowIds(showIds);
            }
        });

        getTrackedFilmsRef().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Long> filmIds = getTrackedFilmIds(task.getResult());
                trackedFilmIds.postValue(filmIds);
                putTrackedFilmIds(filmIds);
            }
        });
    }

    /**
     * Store the tracked tv show ids in sharedPreferences
     *
     * @param ids the ids to store.
     */
    private void putTrackedShowIds(ArrayList<Long> ids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        editor.putString(PREF_TRACKED_SHOWS, gson.toJson(ids));
        editor.apply();
    }

    /**
     * Store the tracked film ids in sharedPreferences
     *
     * @param ids the ids to store.
     */
    private void putTrackedFilmIds(ArrayList<Long> ids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        editor.putString(PREF_TRACKED_FILMS, gson.toJson(ids));
        editor.apply();
    }

    /**
     * Gets a LiveData list of Tracked Show Ids
     *
     * @return a list of tracked tv show ids
     */
    public LiveData<ArrayList<Long>> getTrackedShowIds() {
        return trackedShowIds;
    }

    /**
     * Gets a LiveData list of Tracked Film Ids
     *
     * @return a list of tracked film ids
     */
    public LiveData<ArrayList<Long>> getTrackedFilmIds() {
        return trackedFilmIds;
    }

    /**
     * Put the user location in Firebase to be used for generating the "popular in your area"
     * fragment
     *
     * @param location the user location
     */
    public void sendUserLocation(Location location) {
        //Need to add some checks to see whether google play services are available on the device
        //before getting location ideally

        Address address = null;

        // get the address
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() == 1) {
                address = addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.getAdminArea() != null && address.getSubAdminArea() != null) {
            this.location = address.getSubAdminArea() + ", " + address.getAdminArea();

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map data = new HashMap<String, String>();
            data.put("Location", this.location);
            db.collection("UserLocation").document(getUserId())
                    .set(data)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "location sent to db"))
                    .addOnFailureListener(e -> Log.d(TAG, "failed to send location to db"));
        }
    }

    /**
     * Returns the location currently stored in the api
     *
     * @return GeoPoint of the location in the api
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets a list of the user's Tracked Shows
     *
     * @param listener a listener to be notified when items are added to the list
     * @return a list of tracked shows, may be empty on first call, listener will be notified when items are added
     */
    public List<TVShow> getTrackedShows(final OnMyLibraryUpdatedListener<TVShow> listener) {
        if (trackedShows == null) {
            trackedShows = new ArrayList();
            getTrackedShowsRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final ArrayList<Long> showIds = getTrackedShowIds(task.getResult());

                    // used for comparison to check when load is complete
                    final ArrayList<TVShow> tmpList = new ArrayList<>();

                    for (Long showId : showIds) {
                        MediaRepository.get(context).getTVShow(showId.intValue()).subscribe(tvShow -> {
                            trackedShows.add(tvShow);
                            listener.itemAdded(tvShow);

                            // check if all shows have been loaded
                            tmpList.add(tvShow);
                            if (tmpList.size() == showIds.size()) {
                                listener.loadComplete();
                            }
                        }, throwable -> {
                            tmpList.add(null);
                            if (tmpList.size() == showIds.size()) {
                                listener.loadComplete();
                            }
                            // TODO: handle error
                        });
                    }
                }
            });
        }
        return trackedShows;
    }

    /**
     * Gets a list of the user's tracked films
     *
     * @param listener a listener to be notified when items are added to the list
     * @return a list of tracked films, may be empty on first call, listener will be notified when items are added
     */
    public List<Film> getTrackedFilms(final OnMyLibraryUpdatedListener<Film> listener) {
        if (trackedFilms == null) {
            trackedFilms = new ArrayList();
            getTrackedFilmsRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final ArrayList<Long> filmIds = getTrackedFilmIds(task.getResult());

                    // used for comparison to check when load is complete
                    final ArrayList<Film> tmpList = new ArrayList<>();

                    for (Long filmId : filmIds) {
                        MediaRepository.get(context).getFilm(filmId.intValue()).subscribe(film -> {
                            trackedFilms.add(film);
                            listener.itemAdded(film);

                            tmpList.add(film);
                            if (tmpList.size() == filmIds.size()) {
                                listener.loadComplete();
                            }
                        }, throwable -> {
                            // TODO: handle failure
                        });
                    }
                }
            });
        }
        return trackedFilms;
    }

    /**
     * Returns an instance of the LocationService in this api
     *
     * @return an instance of {@link LocationService}
     */
    public LocationService getLocationService() {
        return locationService;
    }

    /**
     * Get tracked show ids from firebase
     *
     * @param document the document to get the ids from
     * @return a list of tracked ids
     */
    private ArrayList<Long> getTrackedShowIds(DocumentSnapshot document) {
        return getListOfTrackedIds(document, "ShowIds");
    }

    /**
     * Get tracked film ids from firebase
     *
     * @param document the document to get the ids from
     * @return a list of tracked ids
     */
    private ArrayList<Long> getTrackedFilmIds(DocumentSnapshot document) {
        return getListOfTrackedIds(document, "MovieIds");
    }

    /**
     * Gets a list of ids that the user has tracked, using a string to determine whether
     * they are for film or tv shows
     *
     * @param document the document to get the ids from
     * @param path     the location in firebase that the ids are stored in
     * @return a list of tracked ids
     */
    private ArrayList<Long> getListOfTrackedIds(DocumentSnapshot document, String path) {
        Map myDoc = document.getData();
        ArrayList<Long> ids = (ArrayList<Long>) myDoc.get(path);
        return ids;
    }

    /**
     * Get a reference to the document containing the user's tracked shows in the firebase
     *
     * @return a reference to the user's tracked shows
     */
    private DocumentReference getTrackedShowsRef() {
        return getTrackedMediaRef("TrackedShows");
    }

    /**
     * Get a reference to the document containing the user's tracked films in the firebase
     *
     * @return a reference to the user's tracked films
     */
    private DocumentReference getTrackedFilmsRef() {
        return getTrackedMediaRef("TrackedMovies");
    }

    /**
     * Gets a reference to the document containing the user's tracked films/shows in the firebase
     * by using a string to determine where it needs to look
     *
     * @param path the location in which the ids are stored in the firebase
     * @return a reference to the user's tracked films/shows
     */
    private DocumentReference getTrackedMediaRef(String path) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myShowsRef = db.collection(path);
        return myShowsRef.document(getUserId());
    }

    /**
     * Get the id of the currently logged in user
     *
     * @return the user's account id in Firebase auth
     */
    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Subscribes to the film of given ID
     *
     * @param film film to subscribe to
     */
    public void subscribeFilm(Film film) {
        ArrayList<Long> trackedFilmIdsTmp = trackedFilmIds.getValue();
        if (!trackedFilmIdsTmp.contains(Long.valueOf(film.getId()))) {
            subscribe(Long.valueOf(film.getId()), "TrackedMovies", "MovieIds");

            trackedFilmIdsTmp.add(Long.valueOf(film.getId()));
            trackedFilmIds.setValue(trackedFilmIdsTmp);
            if (trackedFilms != null) {
                trackedFilms.add(film);
            }
            putTrackedFilmIds(trackedFilmIdsTmp);
            MediaRepository.get(context).putFilm(film);
        }
    }

    /**
     * Subscribes to the show of given ID
     *
     * @param tvShow show to subscribe to
     */
    public void subscribeShow(TVShow tvShow) {
        ArrayList<Long> trackedShowIdsTmp = trackedShowIds.getValue();
        if (!trackedShowIdsTmp.contains(Long.valueOf(tvShow.getId()))) {
            subscribe(Long.valueOf(tvShow.getId()), "TrackedShows", "ShowIds");

            trackedShowIdsTmp.add(Long.valueOf(tvShow.getId()));
            trackedShowIds.setValue(trackedShowIdsTmp);
            if (trackedShows != null) {
                trackedShows.add(tvShow);
            }
            putTrackedShowIds(trackedShowIdsTmp);
            MediaRepository.get(context).putTVShow(tvShow);
        }
    }

    /**
     * Sends the data of the show you wish to subscribe to to the Firebase document of the User,
     * this is used for both Films and Shows, and simply takes 2 String parameters that will decide
     * what document the information is being sent to
     *
     * @param id             ID of the film/show clicked on in app
     * @param collectionName The name of the collection that the data is being sent to
     * @param varName        The variable that is being updated in the document
     */
    private void subscribe(final Long id, final String collectionName, final String varName) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myShowsRef = db.collection(collectionName);
        myShowsRef.document(getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ArrayList ids = new ArrayList<Long>();
                    if (document.exists()) {
                        ids = getListOfTrackedIds(document, varName);
                    }
                    if (!ids.contains(id)) {
                        ids.add(id);
                    }
                    Map trackedShows = new HashMap<String, ArrayList<Long>>();
                    trackedShows.put(varName, ids);
                    db.collection(collectionName).document(getUserId())
                            .set(trackedShows)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("Successfully sent to Firestore");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Failed to send data to Firestore");
                                }
                            });
                } else {
                    //error
                    System.out.println("Third Tier");
                }
            }
        });

    }

    /**
     * Unsubscribes from the film of given ID
     *
     * @param film to unsubscribe from
     */
    public void unsubscribeFilm(Film film) {
        ArrayList<Long> trackedFilmsTmp = trackedFilmIds.getValue();
        if (trackedFilmsTmp.contains(Long.valueOf(film.getId()))) {
            trackedFilmsTmp.remove(Long.valueOf(film.getId()));
            trackedFilmIds.setValue(trackedFilmsTmp);
            if (trackedFilms != null) {
                trackedFilms.remove(film);
            }
            putTrackedFilmIds(trackedFilmsTmp);
            MediaRepository.get(context).deleteFilm(film.getId());
            unsubscribe(Long.valueOf(film.getId()), "TrackedMovies", "MovieIds");
        }
    }

    /**
     * Unsubscribes from the show of given ID
     *
     * @param tvShow show to unsubscribe from
     */
    public void unsubscribeShow(TVShow tvShow) {
        ArrayList<Long> trackedShowsTmp = trackedShowIds.getValue();
        if (trackedShowsTmp.contains(Long.valueOf(tvShow.getId()))) {
            trackedShowsTmp.remove(Long.valueOf(tvShow.getId()));
            trackedShowIds.setValue(trackedShowsTmp);
            if (trackedShows != null) {
                trackedShows.remove(tvShow);
            }
            putTrackedShowIds(trackedShowsTmp);
            MediaRepository.get(context).deleteTVShow(tvShow.getId());
            unsubscribe(Long.valueOf(tvShow.getId()), "TrackedShows", "ShowIds");
        }
    }

    /**
     * Gets the list of tracked ids for the user from the Firebase, removes the id given that wants
     * to be removed, and updates the document for that user to remove the id from the list
     *
     * @param id             ID of the film/show clicked on in app
     * @param collectionName The name of the collection that the data is being sent to
     * @param varName        The variable that is being updated in the document
     */
    private void unsubscribe(final Long id, final String collectionName, final String varName) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myShowsRef = db.collection(collectionName);
        myShowsRef.document(getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList ids = getListOfTrackedIds(document, varName);
                        ids.remove(id);
                        Map trackedShows = new HashMap<String, ArrayList<Long>>();
                        trackedShows.put(varName, ids);
                        db.collection(collectionName).document(getUserId())
                                .set(trackedShows)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("Successfully sent to Firestore");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Failed to send data to Firestore");
                                    }
                                });
                    } else {
                        System.out.println("Document does not exist in the database");
                    }

                } else {
                    //error
                    System.out.println("Third Tier");
                }
            }
        });
    }

    /**
     * Posts a Review for a TV show to Firebase
     *
     * @param rating     Rating of the show out of 10
     * @param reviewText Written review by the user (allowed to be null)
     * @param showId     ID of the show that's being reviewed
     */
    public void postTVReview(Long rating, String reviewText, Long showId) {
        postReview(rating, reviewText, showId, "TvReviews");
    }

    /**
     * Posts a Review for a Film to the Firebase
     *
     * @param rating     Rating of the film out of 10
     * @param reviewText Written review by the user (allowed to be null)
     * @param filmId     ID of the film that's being reviewed
     */
    public void postFilmReview(Long rating, String reviewText, Long filmId) {
        postReview(rating, reviewText, filmId, "MovieReviews");
    }

    /**
     * Posts a Review of the item currently being viewed to the Firebase
     *
     * @param rating     Rating of the film out of 10
     * @param reviewText Written review by the user (allowed to be null)
     * @param id         ID of the tiem that's being reviewed
     * @param path       Name of the collection the review will be posted to
     */
    private void postReview(Long rating, String reviewText, Long id, String path) {
        getReviewsFor(id, path, new OnReviewAddedListener() {
            @Override
            public void reviewsLoaded() {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> data = new HashMap<>();
                data.put("authorId", getUserId());
                data.put("itemId", id);
                data.put("rating", rating);
                data.put("reviewText", reviewText);
                UserReview myReview = getYourReview();

                //if myReview has an AuthorId of "" then no review exists for this item for this user
                if (myReview.getAuthorId() == "") {
                    db.collection(path)
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.w("postReview", "Document written with id " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("postReview", "Error adding document", e);
                                }
                            });
                } else {
                    //Update the review
                    //Probably need to have a pop-up here to ask if you definitely want to update your review
                    db.collection(path).document(myReview.getFileId()).set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });

    }

    /**
     * Deletes the User's TV Review
     *
     * @param id ID of the TV Show
     */
    public void deleteTVReview(Long id) {
        deleteReview(id, "TvReviews");
    }

    /**
     * Deletes the User's Film Review
     *
     * @param id ID of the Film
     */
    public void deleteFilmReview(Long id) {
        deleteReview(id, "MovieReviews");
    }

    /**
     * Deletes the User's Review for the specified item
     *
     * @param id   ID of the item
     * @param path Path to the Collection the review is stored ingit p
     */
    private void deleteReview(Long id, String path) {
        //Should add a confirmation pop-up when this is called to make sure the user actually wants to delete the review
        getReviewsFor(id, path, new OnReviewAddedListener() {
            @Override
            public void reviewsLoaded() {
                UserReview toDelete = getYourReview();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(path).document(toDelete.getFileId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Should add a pop-up after to show that deletion was a success
                                Log.w("DEL", "Review has successfully been deleted");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("DEL ERR", "Deletion of this document has failed", e);
                            }
                        });
            }
        });
    }

    /**
     * Finds reviews for the specified item in the firebase
     *
     * @param id   ID of the item you're viewing
     * @param path Name of the Collection you are accessing
     * @return Returns a list of Reviews for the specified item
     */
    public void getReviewsFor(Long id, String path, final OnReviewAddedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userReviews.clear();
        db.collection(path).whereEqualTo("itemId", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int counter = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userReviews.add(document.toObject(UserReview.class));
                        userReviews.get(counter).setFileId(document.getId());
                        counter++;
                    }
                } else {
                    Log.w("ERR", "Reviews were not pulled from Firebase");
                }
                reviewList = new ReviewList(userReviews, id);
                listener.reviewsLoaded();
            }
        });
    }

    /**
     * Gets a review that was written by the currently logged in user from the firebase from a list
     * of reviews
     *
     * @return Returns the user's own Review for the specified item
     */
    private UserReview getYourReview() {
        UserReview outputReview = new UserReview();

        if (reviewList.getReviews().size() != 0) {
            for (UserReview review : reviewList.getReviews()) {
                if (review.getAuthorId().equals(getUserId())) {
                    outputReview = review;
                }
            }
        }

        return outputReview;
    }
}
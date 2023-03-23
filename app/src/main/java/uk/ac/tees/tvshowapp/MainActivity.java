package uk.ac.tees.tvshowapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.tees.tvshowapp.firebase.FirebaseApi;
import uk.ac.tees.tvshowapp.notifications.NotificationService;
import uk.ac.tees.tvshowapp.tmdb.TmdbApi;
import uk.ac.tees.tvshowapp.tmdb.model.Episode;
import uk.ac.tees.tvshowapp.tmdb.model.Film;
import uk.ac.tees.tvshowapp.tmdb.model.SearchResults;
import uk.ac.tees.tvshowapp.tmdb.model.TVShow;
import uk.ac.tees.tvshowapp.util.NavigationUtil;

import static uk.ac.tees.tvshowapp.fragments.TVShowDetailsFragment.ARG_TVSHOW;

/**
 * The Main Activity of the overall app, this handles the creation of most fragments seen in the
 * app, as they are essentially created by using the main activity as the canvas for them to be
 * drawn onto. Also handles other general functions of the app that will need to be activated after
 * you log in. This activity will run from when you log in, until you log out.
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private FusedLocationProviderClient locationClient;
    private final int LOCATION_REQUEST_CODE = 121;
    private static Location userLocation;
    private SensorManager sensorMgr;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Trending Shows");

        // handle any intents
        handleIntent();

        // initialize ads and load banner ad (comment out to hide)
        initializeAds();

        // get location permissions form the user
        getLocationPermissions();

        // schedule notification service
        NotificationService.scheduleService(getApplicationContext());

        // setup navigation
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.TrendingShows, R.id.MyShows, R.id.SearchShows,
                R.id.TrendingFilms, R.id.DiscoverShows, R.id.DiscoverFilms, R.id.MyFilms, R.id.SearchFilms,
                R.id.TrendingPeople, R.id.SearchPeople, R.id.LocalTV, R.id.LocalFilms)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // setup invite friend option
        navigationView.getMenu().findItem(R.id.InviteFriend).setOnMenuItemClickListener(item -> {
            String message = String.format(getString(R.string.invite_contact_message), getString(R.string.app_name), getString(R.string.PLAY_STORE_LINK));

            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:"));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
            return true;
        });

        initializeShakeDetector();
    }


    /**
     * Start listening for device shake
     */
    private void initializeShakeDetector() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(count -> {
            if (navController.getCurrentDestination().getLabel().equals("Trending Films")) {
                showRandomTrendingFilm();
            } else if (navController.getCurrentDestination().getLabel().equals("Trending Shows")) {
                showRandomTrendingTVShow();
            }
        });
    }

    // initialize ads and load banner ad
    private void initializeAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * Tells the main activity what to do when it resumes from a pause
     */
    @Override
    public void onResume() {
        super.onResume();
        // register the Sensor Manager Listener onResume
        sensorMgr.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Tells the main activity what to do when it is paused
     */
    @Override
    public void onPause() {
        // unregister the Sensor Manager onPause
        sensorMgr.unregisterListener(shakeDetector);
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Navigate to the appropriate destination needed to handle the specific intent
     */
    private void handleIntent() {
        Intent intent = getIntent();
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case "uk.ac.tees.tvshowapp.film_details":
                    handleFilmDetailsIntent(intent);
                    break;
                case "uk.ac.tees.tvshowapp.episode_details":
                    handleEpisodeDetailsIntent(intent);
                    break;
            }
        }
    }

    /**
     * Request location permissions from the user
     */
    private void getLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Could possibly show the user why we need their permission here
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        } else {
            // Permission has already been granted
            sendLocation();
        }
    }

    /**
     * Gets the user's current location and sends it off to the firebase,
     * so that it can be stored for later usage within the app
     */
    private void sendLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLocation = location;
                            FirebaseApi.get(getApplicationContext()).sendUserLocation(location);
                        } else {
                            requestNewLocationData();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FAIL", "Accessing of Location has failed");
                    }
                });

    }

    /**
     * Sets up a Location Request to retrieve the location of the device
     */
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    /**
     * Callback to respond back to requestNewLocationData() when the data has finall been recieved,
     * this then sends the actual data to the sendUserLocation() method
     */
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                //Can  probably implement this to go to a lower quality
                Log.w("F", "LocationResult is null");
            } else {
                userLocation = locationResult.getLocations().get(0);
                FirebaseApi.get(getApplicationContext()).sendUserLocation(userLocation);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendLocation();
                }
            }
        }
    }


    /**
     * Navigate to the Film Details when a specified notification is pressed
     *
     * @param intent the Intent created by clicking on the notification
     */
    private void handleFilmDetailsIntent(Intent intent) {
        final Activity activity = this;
        int filmId = intent.getIntExtra("FilmId", -1);
        if (filmId != -1) {
            TmdbApi.get().getFilmService().getFilm(filmId).enqueue(new Callback<Film>() {
                @Override
                public void onResponse(Call<Film> call, Response<Film> response) {
                    if (response.isSuccessful()) {
                        // go to film details
                        NavigationUtil.navigateToFilmDetails(findViewById(R.id.fragment_container), response.body());
                    }
                }

                @Override
                public void onFailure(Call<Film> call, Throwable t) {
                    // TODO: handle failure;
                }
            });
        }
    }

    /**
     * Navigate to the Episode Details when a specified notification is pressed
     *
     * @param intent the Intent created by clicking on the notification
     */
    private void handleEpisodeDetailsIntent(Intent intent) {
        final Activity activity = this;
        final int tvShowId = intent.getIntExtra("tvShowId", -1);
        final String tvShowName = intent.getStringExtra("tvShowName");
        final int seasonNumber = intent.getIntExtra("seasonNumber", -1);
        final int episodeNumber = intent.getIntExtra("episodeNumber", -1);

        if (tvShowId != -1 && seasonNumber != -1 && episodeNumber != -1 && tvShowName != null) {
            TmdbApi.get().getTVService().getTVShow(tvShowId).enqueue(new Callback<TVShow>() {
                @Override
                public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                    if (response.isSuccessful()) {
                        final TVShow tvShow = response.body();

                        TmdbApi.get().getTVService().getEpisode(tvShowId, seasonNumber, episodeNumber).enqueue(new Callback<Episode>() {
                            @Override
                            public void onResponse(Call<Episode> call, Response<Episode> response) {
                                if (response.isSuccessful()) {
                                    // go to show details
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(ARG_TVSHOW, tvShow);
                                    bundle.putSerializable("title", tvShowName);
                                    Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.TVShowDetails, bundle);

                                    //immediately go to episode details
                                    NavigationUtil.navigateToEpisodeDetails(findViewById(R.id.fragment_container), tvShow.getName(), response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<Episode> call, Throwable t) {
                                // TODO: handle failure;
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<TVShow> call, Throwable t) {
                    // TODO: handle failure
                }
            });
        }
    }

    /**
     * Gets a list of Trending Films, then selects one at random and sends it be displayed on a
     * fragment
     */
    public void showRandomTrendingFilm() {
        Random random = new Random();
        TmdbApi.get().getFilmService().getTrending(1).enqueue(new Callback<SearchResults<Film>>() {
            @Override
            public void onResponse(Call<SearchResults<Film>> call, Response<SearchResults<Film>> response) {
                if (response.isSuccessful()) {
                    Film film = response.body().getResults().get(random.nextInt(response.body().getResults().size()));
                    NavigationUtil.navigateToFilmDetails(findViewById(R.id.fragment_container), film);
                }
            }

            @Override
            public void onFailure(Call<SearchResults<Film>> call, Throwable t) {
                Log.w("FAIL", "Call Failed");
            }
        });
    }

    /**
     * Gets a list of Trending TV Shows, then selects one at random and sends it be displayed on a
     * fragment
     */
    public void showRandomTrendingTVShow() {
        Random random = new Random();
        TmdbApi.get().getTVService().getTrending(1).enqueue(new Callback<SearchResults<TVShow>>() {
            @Override
            public void onResponse(Call<SearchResults<TVShow>> call, Response<SearchResults<TVShow>> response) {
                if (response.isSuccessful()) {
                    TVShow show = response.body().getResults().get(random.nextInt(response.body().getResults().size()));
                    NavigationUtil.navigateToShowDetails(findViewById(R.id.fragment_container), show);
                }
            }

            @Override
            public void onFailure(Call<SearchResults<TVShow>> call, Throwable t) {
                Log.w("FAIL", "Call Failed");
            }
        });
    }
}
package uk.ac.tees.tvshowapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.firebase.ui.auth.AuthUI;

import uk.ac.tees.tvshowapp.FireBaseAuthActivity;
import uk.ac.tees.tvshowapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static String PREF_FILM_NOTIFICATIONS = "filmNotifications";
    public static String PREF_FILM_NOTIFICATIONS_TIME = "filmNotificationsTime";

    public static String PREF_TVSHOW_NOTIFICATIONS = "tvShowNotifications";
    public static String PREF_TVSHOW_NOTIFICATIONS_TIME = "tvShowNotificationsTime";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        final Context context = getContext();

        findPreference("log_out").setOnPreferenceClickListener(preference -> {
            AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent(context, FireBaseAuthActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    });
            return true;
        });

        SwitchPreference filmNotificationSwitch = findPreference("film_notifications");
        filmNotificationSwitch.setChecked(sharedPreferences.getBoolean(PREF_FILM_NOTIFICATIONS, true));
        filmNotificationSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_FILM_NOTIFICATIONS, (boolean) newValue);
            editor.apply();
            return true;
        });

        ListPreference filmNotificationsTime = findPreference("film_notifications_time");
        filmNotificationsTime.setValue(sharedPreferences.getString(PREF_FILM_NOTIFICATIONS_TIME, "0"));
        filmNotificationsTime.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_FILM_NOTIFICATIONS_TIME, (String) newValue);
            editor.apply();
            return true;
        });

        SwitchPreference tvNotificationSwitch = findPreference("tvshow_notifications");
        tvNotificationSwitch.setChecked(sharedPreferences.getBoolean(PREF_TVSHOW_NOTIFICATIONS, true));
        tvNotificationSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_TVSHOW_NOTIFICATIONS, (boolean) newValue);
            editor.apply();
            return true;
        });

        ListPreference tvShowNotificationsTime = findPreference("tvshow_notifications_time");
        tvShowNotificationsTime.setValue(sharedPreferences.getString(PREF_TVSHOW_NOTIFICATIONS_TIME, "0"));
        tvShowNotificationsTime.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_TVSHOW_NOTIFICATIONS_TIME, (String) newValue);
            editor.apply();
            return true;
        });
    }


}

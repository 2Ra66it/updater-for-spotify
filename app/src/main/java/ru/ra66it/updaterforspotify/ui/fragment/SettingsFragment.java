package ru.ra66it.updaterforspotify.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;

import javax.inject.Inject;

import ru.ra66it.updaterforspotify.BuildConfig;
import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.storage.QueryPreferences;


/**
 * Created by 2Rabbit on 29.09.2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    CheckBoxPreference prefEnableNotificationsOrigin;
    SwitchPreference prefDownloadBeta;
    Preference prefAppInfo;
    SharedPreferences prefs;

    @Inject
    QueryPreferences queryPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getApplicationComponent().inject(this);
        addPreferencesFromResource(R.xml.settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefEnableNotificationsOrigin = (CheckBoxPreference) findPreference("autoOrigin");
        prefEnableNotificationsOrigin.setChecked(queryPreferences.getNotificationOrigin());
        prefEnableNotificationsOrigin.setOnPreferenceClickListener(preference -> {
            if (prefEnableNotificationsOrigin.isChecked()) {
                prefDownloadBeta.setChecked(false);
            }
            return false;
        });


        prefDownloadBeta = (SwitchPreference) findPreference("downloadBeta");
        prefDownloadBeta.setChecked(queryPreferences.isSpotifyBeta());
        prefDownloadBeta.setOnPreferenceClickListener(preference -> {
            if (prefDownloadBeta.isChecked()) {
                showDialogWithTitle(getString(R.string.spotify_beta_available), getString(R.string.if_have_not_beta));
            } else {
                showDialogWithTitle(getString(R.string.spotify_origin_is_available), getString(R.string.if_have_beta));
            }
            return false;
        });


        prefAppInfo = findPreference("verPref");
        prefAppInfo.setSummary(BuildConfig.VERSION_NAME);

    }


    private void showDialogWithTitle(String title, String message) {
        new AlertDialog.Builder(getActivity(), R.style.AlertTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        queryPreferences.setNotificationOrigin(
                prefEnableNotificationsOrigin.isChecked());

        queryPreferences.setSpotifyBeta(prefDownloadBeta.isChecked());
    }


    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}

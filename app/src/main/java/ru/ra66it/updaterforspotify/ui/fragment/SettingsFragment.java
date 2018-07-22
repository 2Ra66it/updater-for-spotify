package ru.ra66it.updaterforspotify.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
    Preference prefAppInfo;
    SharedPreferences prefs;

    @Inject
    QueryPreferences queryPreferences;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getApplicationComponent().inject(this);
        addPreferencesFromResource(R.xml.settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefEnableNotificationsOrigin = (CheckBoxPreference) findPreference("autoOrigin");
        prefEnableNotificationsOrigin.setChecked(queryPreferences.getNotifications());

        prefAppInfo = findPreference("verPref");
        prefAppInfo.setSummary(BuildConfig.VERSION_NAME);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        queryPreferences.setNotifications(
                prefEnableNotificationsOrigin.isChecked());

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

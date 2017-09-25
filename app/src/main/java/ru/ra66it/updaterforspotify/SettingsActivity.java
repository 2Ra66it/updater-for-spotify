package ru.ra66it.updaterforspotify;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

/**
 * Created by 2Rabbit on 25.09.2017.
 */

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    CheckBoxPreference prefEnableNotifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName("Updater");
        addPreferencesFromResource(R.xml.settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        prefEnableNotifications = (CheckBoxPreference) findPreference("autoSwitch");

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(getSharedPreferences("Updater",MODE_PRIVATE).getBoolean("autoSwitch",true)) {
            SpotifyService.setServiceAlarm(getApplicationContext(), true);
        } else {
            SpotifyService.setServiceAlarm(getApplicationContext(), false);
        }
    }
}

package ru.ra66it.updaterforspotify.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import ru.ra66it.updaterforspotify.QueryPreferneces;
import ru.ra66it.updaterforspotify.R;

/**
 * Created by 2Rabbit on 29.09.2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    CheckBoxPreference prefEnableNotificationsDF;
    CheckBoxPreference prefEnableNotificationsOrigin;
    SharedPreferences prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefEnableNotificationsOrigin = (CheckBoxPreference) findPreference("autoOrigin");
        prefEnableNotificationsOrigin.setChecked(QueryPreferneces.getNotificationOrigin(getActivity()));
        prefEnableNotificationsOrigin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (prefEnableNotificationsOrigin.isChecked()) {
                    prefEnableNotificationsDF.setChecked(false);
                }

                return false;
            }
        });

        prefEnableNotificationsDF = (CheckBoxPreference) findPreference("autoDf");
        prefEnableNotificationsDF.setChecked(QueryPreferneces.getNotificationDogFood(getActivity()));
        prefEnableNotificationsDF.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (prefEnableNotificationsDF.isChecked()) {
                    prefEnableNotificationsOrigin.setChecked(false);
                }

                return false;
            }
        });



    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        QueryPreferneces.setNotificationDogFood(getActivity(),
                prefEnableNotificationsDF.isChecked());

        QueryPreferneces.setNotificationOrigin(getActivity(),
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

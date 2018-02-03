package ru.ra66it.updaterforspotify.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import ru.ra66it.updaterforspotify.BuildConfig;
import ru.ra66it.updaterforspotify.storage.QueryPreferneces;
import ru.ra66it.updaterforspotify.R;


/**
 * Created by 2Rabbit on 29.09.2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    CheckBoxPreference prefEnableNotificationsDF;
    CheckBoxPreference prefEnableNotificationsOrigin;
    SwitchPreference prefDownloadBeta;
    Preference prefAppInfo;
    SharedPreferences prefs;
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefEnableNotificationsOrigin = (CheckBoxPreference) findPreference("autoOrigin");
        prefEnableNotificationsOrigin.setChecked(QueryPreferneces.getNotificationOrigin(getActivity()));
        prefEnableNotificationsOrigin.setOnPreferenceClickListener(preference -> {
            if (prefEnableNotificationsOrigin.isChecked()) {
                prefEnableNotificationsDF.setChecked(false);
            } else {
                prefDownloadBeta.setChecked(false);
            }

            return false;
        });

        prefEnableNotificationsDF = (CheckBoxPreference) findPreference("autoDf");
        prefEnableNotificationsDF.setChecked(QueryPreferneces.getNotificationDogFood(getActivity()));
        prefEnableNotificationsDF.setOnPreferenceClickListener(preference -> {
            if (prefEnableNotificationsDF.isChecked()) {
                prefEnableNotificationsOrigin.setChecked(false);
            }

            return false;
        });

        prefDownloadBeta = (SwitchPreference) findPreference("downloadBeta");
        prefDownloadBeta.setChecked(QueryPreferneces.isSpotifyBeta(getActivity()));
        prefDownloadBeta.setOnPreferenceClickListener(preference -> {
            if (prefDownloadBeta.isChecked()) {
                showDialog(getString(R.string.if_have_not_beta));
            } else {
                showDialog(getString(R.string.if_have_beta));
            }
            return false;
        });


        prefAppInfo = findPreference("verPref");
        prefAppInfo.setSummary(BuildConfig.VERSION_NAME);
        prefAppInfo.setOnPreferenceClickListener(preference -> {
            i++;
            if (i >= 6) {
                QueryPreferneces.setNotificationDogFoodC(getContext(), !QueryPreferneces.getNotificationDogFoodC(getContext()));
                if (QueryPreferneces.getNotificationDogFoodC(getContext())) {
                    showDialogWithTitle(getString(R.string.dfc_is_available), getString(R.string.if_have_dogfood));
                } else {
                    showDialogWithTitle(getString(R.string.dfc_is_not_available), getString(R.string.if_have_dogfoodc));
                }
                i = 0;
            }

            return true;
        });

    }

    private void showDialog(String message) {
        new AlertDialog.Builder(getActivity(), R.style.AlertTheme)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .show();
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
        QueryPreferneces.setNotificationDogFood(getActivity(),
                prefEnableNotificationsDF.isChecked());

        QueryPreferneces.setNotificationOrigin(getActivity(),
                prefEnableNotificationsOrigin.isChecked());

        QueryPreferneces.setSpotifyBeta(getActivity(),
                prefDownloadBeta.isChecked());
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

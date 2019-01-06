package ru.ra66it.updaterforspotify.presentation.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences
import javax.inject.Inject

/**
 * Created by 2Rabbit on 29.09.2017.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefEnableNotificationsOrigin: CheckBoxPreference
    private lateinit var prefAppInfo: Preference
    lateinit var prefs: SharedPreferences

    @Inject lateinit var queryPreferences: QueryPreferences

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        UpdaterApp.applicationComponent.inject(this)
        addPreferencesFromResource(R.xml.settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        prefEnableNotificationsOrigin = findPreference("autoOrigin") as CheckBoxPreference
        prefEnableNotificationsOrigin.isChecked = queryPreferences.isEnableNotification

        prefAppInfo = findPreference("verPref") as Preference
        prefAppInfo.summary = BuildConfig.VERSION_NAME
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        queryPreferences.isEnableNotification = prefEnableNotificationsOrigin.isChecked
    }

    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}

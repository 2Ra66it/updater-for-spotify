package ru.ra66it.updaterforspotify.presentation.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences
import javax.inject.Inject

/**
 * Created by 2Rabbit on 29.09.2017.
 */

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var prefEnableNotificationsOrigin: CheckBoxPreference
    lateinit var prefAppInfo: Preference
    lateinit var prefs: SharedPreferences

    @Inject lateinit var queryPreferences: QueryPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdaterApp.applicationComponent.inject(this)
        addPreferencesFromResource(R.xml.settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        prefEnableNotificationsOrigin = findPreference("autoOrigin") as CheckBoxPreference
        prefEnableNotificationsOrigin.isChecked = queryPreferences.isEnableNotification

        prefAppInfo = findPreference("verPref")
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

package ru.ra66it.updaterforspotify.presentation.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import javax.inject.Inject

/**
 * Created by 2Rabbit on 29.09.2017.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var prefEnableNotificationsOrigin: CheckBoxPreference? = null
    private var prefCheckInterval: ListPreference? = null
    private var prefAppInfo: Preference? = null
    private lateinit var prefs: SharedPreferences

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings)
        UpdaterApp.applicationComponent.inject(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        prefEnableNotificationsOrigin = findPreference("updateCheckBox")
        prefEnableNotificationsOrigin?.isChecked = sharedPreferencesHelper.isEnableNotification

        prefCheckInterval = findPreference("updateInterval")
        prefCheckInterval?.value = sharedPreferencesHelper.checkIntervalDay.toString()

        prefAppInfo = findPreference("verPref")
        prefAppInfo?.summary = BuildConfig.VERSION_NAME
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        prefEnableNotificationsOrigin?.let {
            sharedPreferencesHelper.isEnableNotification = it.isChecked
        }
        prefCheckInterval?.let {
            sharedPreferencesHelper.checkIntervalDay = it.value.toLong()
        }
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

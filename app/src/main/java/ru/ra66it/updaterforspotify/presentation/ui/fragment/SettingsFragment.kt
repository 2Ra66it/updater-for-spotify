package ru.ra66it.updaterforspotify.presentation.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.ext.android.inject
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper

/**
 * Created by 2Rabbit on 29.09.2017.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefEnableNotificationsOrigin: CheckBoxPreference
    private lateinit var prefCheckInterval: ListPreference
    private lateinit var prefAppInfo: Preference
    private lateinit var prefs: SharedPreferences

    private val sharedPreferencesHelper: SharedPreferencesHelper by inject()

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        prefEnableNotificationsOrigin = findPreference("updateCheckBox") as CheckBoxPreference
        prefEnableNotificationsOrigin.isChecked = sharedPreferencesHelper.isEnableNotification

        prefCheckInterval = findPreference("updateInterval") as ListPreference
        prefCheckInterval.value = sharedPreferencesHelper.checkIntervalDay.toString()

        prefAppInfo = findPreference("verPref") as Preference
        prefAppInfo.summary = BuildConfig.VERSION_NAME
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        sharedPreferencesHelper.isEnableNotification = prefEnableNotificationsOrigin.isChecked
        sharedPreferencesHelper.checkIntervalDay = prefCheckInterval.value.toLong()
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

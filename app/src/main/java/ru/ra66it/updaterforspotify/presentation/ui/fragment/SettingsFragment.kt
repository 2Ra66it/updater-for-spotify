package ru.ra66it.updaterforspotify.presentation.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import androidx.work.ExistingPeriodicWorkPolicy
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import javax.inject.Inject

/**
 * Created by 2Rabbit on 29.09.2017.
 */

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    @Inject
    lateinit var workersManager: WorkersEnqueueManager

    private var prefEnableNotificationsOrigin: CheckBoxPreference? = null
    private var prefCheckInterval: ListPreference? = null
    private var prefAppInfo: Preference? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings)
        UpdaterApp.applicationComponent.inject(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        prefEnableNotificationsOrigin = findPreference("updateCheckBox")
        prefEnableNotificationsOrigin?.let { enablePreference ->
            enablePreference.isChecked = sharedPreferencesHelper.isEnableNotification
            enablePreference.setOnPreferenceChangeListener { preference, newValue ->
                if (newValue is Boolean) {
                    sharedPreferencesHelper.isEnableNotification = newValue
                    if (newValue) {
                        workersManager.enqueuePeriodicChecking(sharedPreferencesHelper.checkIntervalDay, ExistingPeriodicWorkPolicy.REPLACE)
                    } else {
                        workersManager.stopPeriodicChecking()
                    }
                }

                true
            }
        }

        prefCheckInterval = findPreference("updateInterval")
        prefCheckInterval?.let { checkInterval ->
            checkInterval.value = sharedPreferencesHelper.checkIntervalDay.toString()
            checkInterval.setOnPreferenceChangeListener { preference, newValue ->
                if (newValue is String) {
                    val days = newValue.toLong()
                    sharedPreferencesHelper.checkIntervalDay = days
                    workersManager.enqueuePeriodicChecking(days, ExistingPeriodicWorkPolicy.REPLACE)
                }

                true
            }
        }

        prefAppInfo = findPreference("verPref")
        prefAppInfo?.summary = BuildConfig.VERSION_NAME
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}

package ru.ra66it.updaterforspotify.presentation.ui.fragment

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.presentation.viewmodel.SettingsViewModel
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    private var prefEnableNotificationsOrigin: CheckBoxPreference? = null
    private var prefCheckInterval: ListPreference? = null
    private var prefAppInfo: Preference? = null

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings)
        UpdaterApp.applicationComponent.inject(this)

        prefEnableNotificationsOrigin = findPreference("updateCheckBox")
        prefEnableNotificationsOrigin?.let { enablePreference ->
            enablePreference.isChecked = viewModel.isEnableNotification
            enablePreference.setOnPreferenceChangeListener { _, newValue ->
                viewModel.toggleEnableNotifications(newValue)
                true
            }
        }

        prefCheckInterval = findPreference("updateInterval")
        prefCheckInterval?.let { checkInterval ->
            checkInterval.value = viewModel.checkIntervalDay
            checkInterval.setOnPreferenceChangeListener { _, newValue ->
                viewModel.updateInterval(newValue)
                true
            }
        }

        prefAppInfo = findPreference("verPref")
        prefAppInfo?.summary = viewModel.versionApp
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}

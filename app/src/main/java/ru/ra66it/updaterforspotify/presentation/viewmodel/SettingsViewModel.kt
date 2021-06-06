package ru.ra66it.updaterforspotify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val workersEnqueueManager: WorkersEnqueueManager
) : ViewModel() {

    val versionApp: String get() = BuildConfig.VERSION_NAME
    val isEnableNotification: Boolean get() = sharedPreferencesHelper.isEnableNotification
    val checkIntervalDay: String get() = sharedPreferencesHelper.checkIntervalDay.toString()

    fun toggleEnableNotifications(newValue: Any) {
        if (newValue is Boolean) {
            sharedPreferencesHelper.isEnableNotification = newValue
            if (newValue) {
                workersEnqueueManager.enqueuePeriodicChecking(
                    sharedPreferencesHelper.checkIntervalDay,
                    ExistingPeriodicWorkPolicy.REPLACE
                )
            } else {
                workersEnqueueManager.stopPeriodicChecking()
            }
        }
    }

    fun updateInterval(newValue: Any) {
        if (newValue is String) {
            val days = newValue.toLong()
            sharedPreferencesHelper.checkIntervalDay = days
            workersEnqueueManager.enqueuePeriodicChecking(days, ExistingPeriodicWorkPolicy.REPLACE)
        }
    }

}
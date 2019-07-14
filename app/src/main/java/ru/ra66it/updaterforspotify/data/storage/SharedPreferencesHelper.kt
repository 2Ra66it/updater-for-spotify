package ru.ra66it.updaterforspotify.data.storage

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Created by 2Rabbit on 24.09.2017.
 */

class SharedPreferencesHelper @Inject constructor(
        private val sharedPreferences: SharedPreferences
) {

    private val prefNotification = "prefNotification"
    private val prefCheckIntervalDay = "prefCheckIntervalDay"

    var isEnableNotification: Boolean
        get() = sharedPreferences.getBoolean(prefNotification, true)
        set(isOn) = sharedPreferences.edit().putBoolean(prefNotification, isOn).apply()

    var checkIntervalDay: Long
        get() = sharedPreferences.getLong(prefCheckIntervalDay, 1)
        set(interval) = sharedPreferences.edit().putLong(prefCheckIntervalDay, interval).apply()
}

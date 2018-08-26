package ru.ra66it.updaterforspotify.data.storage

import android.content.SharedPreferences

import javax.inject.Inject

/**
 * Created by 2Rabbit on 24.09.2017.
 */

class QueryPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val prefNotification = "pref_notification"
    private val prefIsFirst = "pref_is_first"

    var isEnableNotification: Boolean
        get() = sharedPreferences.getBoolean(prefNotification, false)
        set(isOn) = sharedPreferences.edit().putBoolean(prefNotification, isOn).apply()

    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(prefIsFirst, true)
        set(isOn) = sharedPreferences.edit().putBoolean(prefIsFirst, isOn).apply()
}

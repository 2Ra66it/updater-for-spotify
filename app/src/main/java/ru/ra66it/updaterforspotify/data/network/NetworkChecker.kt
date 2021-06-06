package ru.ra66it.updaterforspotify.data.network

import android.content.Context
import android.net.ConnectivityManager
import ru.ra66it.updaterforspotify.UpdaterApp

object NetworkChecker {
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager =
                UpdaterApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
}

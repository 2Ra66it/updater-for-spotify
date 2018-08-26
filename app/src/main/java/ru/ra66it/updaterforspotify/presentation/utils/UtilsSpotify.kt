package ru.ra66it.updaterforspotify.presentation.utils

import android.content.pm.PackageManager
import ru.ra66it.updaterforspotify.UpdaterApp

/**
 * Created by 2Rabbit on 20.09.2017.
 */

object UtilsSpotify {

    private val TAG = UtilsSpotify::class.java.simpleName

    val isSpotifyInstalled: Boolean
        get() {
            return try {
                UpdaterApp.instance.packageManager.getPackageInfo("com.spotify.music", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    val installedSpotifyVersion: String
        get() {
            return try {
                UpdaterApp.instance.packageManager.getPackageInfo("com.spotify.music", 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
               "0"
            }
        }


    fun isSpotifyUpdateAvailable(installedVersion: String, latestedVersion: String): Boolean {
        val installVersion = Integer.parseInt(installedVersion.replace("[^0-9]".toRegex(), "").replace("\\s+".toRegex(), ""))
        val latestVersion = Integer.parseInt(latestedVersion.replace("[.]".toRegex(), "").replace("\\s+".toRegex(), ""))
        return installVersion < latestVersion
    }
}

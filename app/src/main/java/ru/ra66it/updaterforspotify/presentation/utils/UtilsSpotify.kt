package ru.ra66it.updaterforspotify.presentation.utils

import android.content.pm.PackageManager
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.spotifyPackage

/**
 * Created by 2Rabbit on 20.09.2017.
 */

object UtilsSpotify {

    val isSpotifyInstalled: Boolean
        get() {
            return try {
                UpdaterApp.instance.packageManager.getPackageInfo(spotifyPackage, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    val installedSpotifyVersion: String
        get() {
            return try {
                UpdaterApp.instance.packageManager.getPackageInfo(spotifyPackage, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                ""
            }
        }

    fun isSpotifyUpdateAvailable(installVersion: String, lastVersion: String): Boolean {
        val installedVersion = convertVersionToInt(installVersion)
        val latestVersion = convertVersionToInt(lastVersion)
        return installedVersion < latestVersion
    }

    fun convertVersionToInt(version: String): Int {
        return Integer.parseInt(version.replace("[^0-9]".toRegex(), ""))
    }
}

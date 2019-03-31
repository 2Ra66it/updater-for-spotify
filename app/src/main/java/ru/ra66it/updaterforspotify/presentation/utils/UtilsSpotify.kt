package ru.ra66it.updaterforspotify.presentation.utils

import android.content.pm.PackageManager
import ru.ra66it.updaterforspotify.*

/**
 * Created by 2Rabbit on 20.09.2017.
 */

object UtilsSpotify {

    fun getSpotifyState(latestVersion: String): Int {
        return if (isSpotifyInstalled) {
            val installedVersion = UpdaterApp.instance.packageManager.getPackageInfo(spotifyPackage, 0).versionName
            val haveUpdate = isSpotifyUpdateAvailable(installedVersion, latestVersion)

            if (haveUpdate) {
                spotifyHaveUpdate
            } else {
                spotifyIsLatest
            }

        } else {
            spotifyNotInstalled
        }
    }

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
                StringService.getById(R.string.spotify_not_installed)
            }
        }

    fun isSpotifyUpdateAvailable(installVersion: String, lastVersion: String): Boolean {
        return compareVersion(installVersion, lastVersion) == 1
    }

    fun compareVersion(installVersion: String, lastVersion: String): Int {
        val firstVersion = installVersion.split("\\.".toRegex())
        val secondVersion = lastVersion.split("\\.".toRegex())
        val length = Math.max(firstVersion.size, secondVersion.size)

        for (i in 0 until length) {
            val firstPart = if (i < firstVersion.size) firstVersion[i].toInt() else 0
            val secondPart = if (i < secondVersion.size) secondVersion[i].toInt() else 0
            when {
                firstPart > secondPart -> return -1
                firstPart < secondPart -> return 1
            }
        }

        return 0
    }
}


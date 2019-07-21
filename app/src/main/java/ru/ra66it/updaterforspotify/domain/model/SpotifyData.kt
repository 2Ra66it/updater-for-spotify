package ru.ra66it.updaterforspotify.domain.model

/**
 * Created by 2Rabbit on 14.01.2018.
 */

data class SpotifyData(
        val latestLink: String,
        val latestVersionName: String,
        val latestVersionNumber: String
) {
    var installedVersion: String = ""
    var spotifyState: Int = 0
    var isDownloading: Boolean = false
}

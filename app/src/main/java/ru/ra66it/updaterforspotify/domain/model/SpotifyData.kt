package ru.ra66it.updaterforspotify.domain.model

data class SpotifyData(
    val latestLink: String = "",
    val latestVersionName: String = "",
    val latestVersionNumber: String = "",
    var installedVersion: String = "",
    var spotifyState: Int = 0
)

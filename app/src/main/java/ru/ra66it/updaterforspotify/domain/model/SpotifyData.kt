package ru.ra66it.updaterforspotify.domain.model

import ru.ra66it.updaterforspotify.spotifyNotInstalled

data class SpotifyData(
    val latestLink: String = "",
    val latestVersionName: String = "",
    val latestVersionNumber: String = "",
    val installedVersion: String = "",
    val spotifyState: Int = spotifyNotInstalled
)

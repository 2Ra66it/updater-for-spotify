package ru.ra66it.updaterforspotify.presentation.utils

import ru.ra66it.updaterforspotify.domain.model.Spotify
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import javax.inject.Inject

class SpotifyMapper @Inject constructor() {

    fun map(spotify: Spotify): SpotifyData {
        val latestLink: String = spotify.data.file.path
        val latestVersionName: String = spotify.data.name + " " + spotify.data.file.vername
        val latestVersionNumber: String = spotify.data.file.vername
        val data = SpotifyData(latestLink, latestVersionName, latestVersionNumber)
        return updateInstalledVersion(data)
    }

    fun updateInstalledVersion(spotifyData: SpotifyData): SpotifyData {
        spotifyData.installedVersion = UtilsSpotify.installedSpotifyVersion
        spotifyData.spotifyState = UtilsSpotify.getSpotifyState(spotifyData.latestVersionNumber)
        return spotifyData
    }

}
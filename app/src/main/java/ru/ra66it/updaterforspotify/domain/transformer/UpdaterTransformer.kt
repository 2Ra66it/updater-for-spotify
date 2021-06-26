package ru.ra66it.updaterforspotify.domain.transformer

import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.domain.model.Spotify
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.presentation.utils.StringService
import ru.ra66it.updaterforspotify.presentation.utils.VersionsComparator
import ru.ra66it.updaterforspotify.spotifyHaveUpdate
import ru.ra66it.updaterforspotify.spotifyIsLatest
import ru.ra66it.updaterforspotify.spotifyNotInstalled
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdaterTransformer @Inject constructor() {

    fun transform(
        spotify: Spotify,
        installedSpotifyVersion: String,
        versionsComparator: VersionsComparator
    ): SpotifyData {
        val latestLink: String = spotify.data.file.path
        val latestVersionName: String = spotify.data.name + " " + spotify.data.file.vername
        val latestVersionNumber: String = spotify.data.file.vername

        val installedVersion = transformInstalledVersion(installedSpotifyVersion)

        val state =
            getSpotifyState(installedSpotifyVersion, latestVersionNumber, versionsComparator)

        return SpotifyData(
            latestLink = latestLink,
            latestVersionName = latestVersionName,
            latestVersionNumber = latestVersionNumber,
            installedVersion = installedVersion,
            spotifyState = state
        )
    }

    fun getSpotifyState(
        installedSpotifyVersion: String,
        latestVersion: String,
        versionsComparator: VersionsComparator
    ): Int {
        return if (installedSpotifyVersion.isNotEmpty()) {
            val haveUpdate =
                versionsComparator.compareVersion(installedSpotifyVersion, latestVersion) == 1
            if (haveUpdate) spotifyHaveUpdate else spotifyIsLatest
        } else {
            spotifyNotInstalled
        }
    }

    fun transformInstalledVersion(installedSpotifyVersion: String): String {
        return if (installedSpotifyVersion.isEmpty())
            StringService.getById(R.string.spotify_not_installed)
        else installedSpotifyVersion
    }

}
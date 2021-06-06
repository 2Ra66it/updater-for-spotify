package ru.ra66it.updaterforspotify.domain.transformer

import ru.ra66it.updaterforspotify.domain.model.Spotify
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdaterTransformer @Inject constructor() {

    fun transform(spotify: Spotify): SpotifyData {
        val latestLink: String = spotify.data.file.path
        val latestVersionName: String = spotify.data.name + " " + spotify.data.file.vername
        val latestVersionNumber: String = spotify.data.file.vername

        return SpotifyData(
            latestLink = latestLink,
            latestVersionName = latestVersionName,
            latestVersionNumber = latestVersionNumber
        )
    }

}
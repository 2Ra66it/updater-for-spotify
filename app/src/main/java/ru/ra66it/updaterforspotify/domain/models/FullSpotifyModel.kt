package ru.ra66it.updaterforspotify.domain.models

/**
 * Created by 2Rabbit on 14.01.2018.
 */

class FullSpotifyModel(spotify: Spotify) {

    val latestLink: String = spotify.data?.file?.path!!
    val latestVersionName: String = spotify.data?.name + " " + spotify.data?.file?.vername
    val latestVersionNumber: String = spotify.data?.file?.vername!!

}

package ru.ra66it.updaterforspotify.domain.model

sealed class SpotifyStatusState {
    data class Loading(val installedVersion: String) : SpotifyStatusState()
    data class Data(val spotify: SpotifyData) : SpotifyStatusState()
    data class Error(val exception: Exception, val installedVersion: String) : SpotifyStatusState()
}
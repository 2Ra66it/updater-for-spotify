package ru.ra66it.updaterforspotify.domain.model

sealed class SpotifyStatusState {
    object Loading : SpotifyStatusState()
    data class Data(val spotify: SpotifyData) : SpotifyStatusState()
    data class Error(val exception: Exception) : SpotifyStatusState()
}
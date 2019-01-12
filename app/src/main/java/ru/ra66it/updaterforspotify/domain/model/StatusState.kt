package ru.ra66it.updaterforspotify.domain.model

sealed class StatusState {
    object Loading : StatusState()
    data class Data(val spotify: SpotifyData) : StatusState()
    data class Error(val exception: Exception) : StatusState()
}
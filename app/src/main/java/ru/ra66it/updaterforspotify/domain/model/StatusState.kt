package ru.ra66it.updaterforspotify.domain.model

sealed class SpotifyStatusState {
    object Loading : SpotifyStatusState()
    data class Data(val spotify: SpotifyData) : SpotifyStatusState()
    data class Error(val exception: Exception) : SpotifyStatusState()
}

sealed class DownloadStatusState {
    object Cancel : DownloadStatusState()
    data class Start(val name: String) : DownloadStatusState()
    data class Progress(val progress: Int, val name: String) : DownloadStatusState()
    data class Complete(val path: String) : DownloadStatusState()
    data class Error(val exception: Exception) : DownloadStatusState()
}
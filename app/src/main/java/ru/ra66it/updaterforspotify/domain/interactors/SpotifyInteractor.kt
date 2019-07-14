package ru.ra66it.updaterforspotify.domain.interactors

import ru.ra66it.updaterforspotify.data.repositories.SpotifyRepository
import ru.ra66it.updaterforspotify.domain.model.Result
import ru.ra66it.updaterforspotify.domain.model.Spotify
import javax.inject.Inject

class SpotifyInteractor @Inject constructor(
        private val spotifyRepository: SpotifyRepository
) {

    suspend fun getSpotify(): Result<Spotify> {
        return spotifyRepository.getSpotify()
    }
}

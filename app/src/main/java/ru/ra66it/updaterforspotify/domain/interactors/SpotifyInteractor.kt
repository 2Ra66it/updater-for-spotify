package ru.ra66it.updaterforspotify.domain.interactors

import ru.ra66it.updaterforspotify.domain.Result
import ru.ra66it.updaterforspotify.domain.model.Spotify
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository
import javax.inject.Inject

class SpotifyInteractor @Inject constructor(private val spotifyRepository: SpotifyRepository) {

    suspend fun getSpotify(): Result<Spotify> {
       return spotifyRepository.getSpotify()
    }
}

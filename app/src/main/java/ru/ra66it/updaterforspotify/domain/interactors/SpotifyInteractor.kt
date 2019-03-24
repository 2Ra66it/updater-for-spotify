package ru.ra66it.updaterforspotify.domain.interactors

import ru.ra66it.updaterforspotify.domain.model.Result
import ru.ra66it.updaterforspotify.domain.model.Spotify
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository

class SpotifyInteractor(private val spotifyRepository: SpotifyRepository) {

    suspend fun getSpotify(): Result<Spotify> {
       return spotifyRepository.getSpotify()
    }
}

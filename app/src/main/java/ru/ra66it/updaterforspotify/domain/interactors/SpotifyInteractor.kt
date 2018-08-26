package ru.ra66it.updaterforspotify.domain.interactors

import io.reactivex.Observable
import ru.ra66it.updaterforspotify.domain.models.Spotify
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository
import javax.inject.Inject

class SpotifyInteractor @Inject constructor(private val spotifyRepository: SpotifyRepository) {

    fun latestSpotify(): Observable<Spotify> {
       return spotifyRepository.latestSpotify()
    }
}

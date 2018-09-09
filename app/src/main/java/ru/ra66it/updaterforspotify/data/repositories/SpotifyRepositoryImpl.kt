package ru.ra66it.updaterforspotify.data.repositories

import io.reactivex.Observable
import ru.ra66it.updaterforspotify.data.network.SpotifyApi
import ru.ra66it.updaterforspotify.domain.model.Spotify
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository
import javax.inject.Inject


class SpotifyRepositoryImpl @Inject constructor(private val spotifyApi: SpotifyApi)
    : SpotifyRepository {

    override fun latestSpotify(): Observable<Spotify> {
        return spotifyApi.latestSpotify()
    }
}

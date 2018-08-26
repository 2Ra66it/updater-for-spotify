package ru.ra66it.updaterforspotify.domain.repositories

import io.reactivex.Observable
import ru.ra66it.updaterforspotify.domain.models.Spotify

interface SpotifyRepository {

    fun latestSpotify(): Observable<Spotify>
}

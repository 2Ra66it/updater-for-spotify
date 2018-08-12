package ru.ra66it.updaterforspotify.domain.repositories

import io.reactivex.Observable
import ru.ra66it.updaterforspotify.domain.models.Spotify

interface SpotifyRepository {

    val latestSpotify: Observable<Spotify>
}

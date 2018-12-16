package ru.ra66it.updaterforspotify.domain.repositories

import ru.ra66it.updaterforspotify.domain.Result
import ru.ra66it.updaterforspotify.domain.model.Spotify

interface SpotifyRepository {

    suspend fun getSpotify(): Result<Spotify>
}

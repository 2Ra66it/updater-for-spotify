package ru.ra66it.updaterforspotify.data.repositories

import ru.ra66it.updaterforspotify.data.network.UpdaterDataSource
import ru.ra66it.updaterforspotify.domain.model.Spotify
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdaterRepository @Inject constructor(
    private val updaterDataSource: UpdaterDataSource
) {

    suspend fun getSpotify(): Spotify = updaterDataSource.latestSpotifyAsync()

}

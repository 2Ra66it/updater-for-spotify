package ru.ra66it.updaterforspotify.data.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.domain.model.Spotify

interface UpdaterDataSource{

    @GET(BuildConfig.SPOTIFY_API)
    fun latestSpotifyAsync(): Deferred<Spotify>
}

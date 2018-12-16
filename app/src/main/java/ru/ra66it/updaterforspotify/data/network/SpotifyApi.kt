package ru.ra66it.updaterforspotify.data.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.domain.model.Spotify

/**
 * Created by 2Rabbit on 22.09.2017.
 */

interface SpotifyApi {

    @GET(BuildConfig.SPOTIFY_API)
    fun latestSpotify(): Deferred<Response<Spotify>>
}

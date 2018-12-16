package ru.ra66it.updaterforspotify.data.repositories

import ru.ra66it.updaterforspotify.data.network.SpotifyApi
import ru.ra66it.updaterforspotify.domain.Result
import ru.ra66it.updaterforspotify.domain.model.Spotify
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository
import ru.ra66it.updaterforspotify.presentation.utils.safeApiCall
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(private val spotifyApi: SpotifyApi)
    : SpotifyRepository {

    override suspend fun getSpotify() = safeApiCall(
            call = { latestSpotify() }
    )

    private suspend fun latestSpotify(): Result<Spotify> {
        val response = spotifyApi.latestSpotify().await()
        if (response.isSuccessful)
            return Result.Success(response.body()!!)
        return Result.Error(Exception(response.errorBody()!!.string()))
    }

}

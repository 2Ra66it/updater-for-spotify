package ru.ra66it.updaterforspotify.data.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import ru.ra66it.updaterforspotify.BuildConfig;
import ru.ra66it.updaterforspotify.di.DaggerApplicationComponent;
import ru.ra66it.updaterforspotify.domain.models.Spotify;

/**
 * Created by 2Rabbit on 22.09.2017.
 */

public interface SpotifyApi {

    @GET(BuildConfig.SPOTIFY_API)
    Observable<Spotify> getLatestSpotify();
}

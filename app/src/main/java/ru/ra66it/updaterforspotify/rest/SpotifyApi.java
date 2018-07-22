package ru.ra66it.updaterforspotify.rest;

import io.reactivex.Observable;
import retrofit2.http.GET;
import ru.ra66it.updaterforspotify.model.Spotify;


/**
 * Created by 2Rabbit on 22.09.2017.
 */

public interface SpotifyApi {

    @GET("api/7/app/getMeta/package_name=com.spotify.music")
    Observable<Spotify> getLatestOrigin();
}

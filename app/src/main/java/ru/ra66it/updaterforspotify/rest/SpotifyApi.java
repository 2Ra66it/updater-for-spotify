package ru.ra66it.updaterforspotify.rest;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import ru.ra66it.updaterforspotify.model.Spotify;


/**
 * Created by 2Rabbit on 22.09.2017.
 */

public interface SpotifyApi {

    @GET("repos/sergiocastell/spotify-dogfood/releases/latest")
    Observable<Spotify> getLatestDogFood();

    @GET("repos/spotify-dogfood/dogfood-core-bin/releases/latest")
    Observable<Spotify> getLatestDogFoodC();

    @GET("repos/spotify-dogfood/spotify-bin/releases/latest")
    Observable<Spotify> getLatestOrigin();

    @GET("repos/spotify-dogfood/spotify-beta-bin/releases/latest")
    Observable<Spotify> getLatestOriginBeta();

}

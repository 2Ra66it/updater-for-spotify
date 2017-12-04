package ru.ra66it.updaterforspotify.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.ra66it.updaterforspotify.rest.RestClient;
import ru.ra66it.updaterforspotify.rest.SpotifyApi;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

@Module
public class RestModule {

    private RestClient restClient;

    public RestModule() {
        restClient = new RestClient();
    }

    @Provides
    @Singleton
    public RestClient provideRestClient() {
        return restClient;
    }

    @Provides
    @Singleton
    SpotifyApi provideSpotifyApi() {
        return restClient.createService(SpotifyApi.class);
    }

}

package ru.ra66it.updaterforspotify.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ra66it.updaterforspotify.BuildConfig;
import ru.ra66it.updaterforspotify.data.network.SpotifyApi;
import ru.ra66it.updaterforspotify.data.repositories.SpotifyRepositoryImpl;
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

@Module
public class NetworkModule {

    @Provides
    @Singleton
    SpotifyRepository provideSpotifyRepository(SpotifyRepositoryImpl spotifyRepository) {
        return spotifyRepository;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient httpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.SPOTIFY_BASE_URL)
                .client(httpClient)
                .build();
    }

    @Provides
    @Singleton
    SpotifyApi provideApi(Retrofit retrofit) {
        return retrofit.create(SpotifyApi.class);
    }

}

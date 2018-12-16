package ru.ra66it.updaterforspotify.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.data.network.SpotifyApi
import ru.ra66it.updaterforspotify.data.repositories.SpotifyRepositoryImpl
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository
import javax.inject.Singleton

/**
 * Created by 2Rabbit on 04.12.2017.
 */

@Module
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideSpotifyRepository(spotifyRepository: SpotifyRepositoryImpl): SpotifyRepository {
        return spotifyRepository
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BuildConfig.SPOTIFY_BASE_URL)
                .client(httpClient)
                .build()
    }

    @Provides
    @Singleton
    internal fun provideApi(retrofit: Retrofit): SpotifyApi {
        return retrofit.create(SpotifyApi::class.java)
    }

}

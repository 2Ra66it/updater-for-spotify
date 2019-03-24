package ru.ra66it.updaterforspotify.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.data.network.SpotifyApi
import ru.ra66it.updaterforspotify.data.repositories.SpotifyRepositoryImpl
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper

/**
 * Created by 2Rabbit on 04.12.2017.
 */

val networkModule = module {
    single { createOkHttpClient() }
    single { createRetrofit(get()) }
    single { createApi(get()) }
    single<SpotifyRepository> { SpotifyRepositoryImpl(get()) }
    single { SpotifyInteractor(get()) }
    single { SpotifyMapper() }
}

fun createOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
}

fun createRetrofit(httpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BuildConfig.SPOTIFY_BASE_URL)
            .client(httpClient)
            .build()
}

fun createApi(retrofit: Retrofit): SpotifyApi {
    return retrofit.create(SpotifyApi::class.java)
}


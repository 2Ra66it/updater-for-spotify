package ru.ra66it.updaterforspotify.di

import android.app.Application
import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.data.network.SpotifyApi
import ru.ra66it.updaterforspotify.data.repositories.SpotifyRepositoryImpl
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.repositories.SpotifyRepository
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper
import ru.ra66it.updaterforspotify.presentation.viewmodel.SpotifyViewModel
import ru.ra66it.updaterforspotify.sharedPreferencesName

/**
 * Created by 2Rabbit on 24.03.2019.
 */

val applicationModule = module {
    single { createSharedPreferences(androidApplication()) }
    viewModel { SpotifyViewModel(get(), get(), get()) }
}

fun createSharedPreferences(androidApplication: Application): SharedPreferencesHelper {
    val sharedPreferences = androidApplication.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    return SharedPreferencesHelper(sharedPreferences)
}

val networkModule = module {
    single { createOkHttpClient() }
    single { createRetrofit(get()) }
    single { createApi(get()) }
    single<SpotifyRepository> { SpotifyRepositoryImpl(get()) }
    single { SpotifyInteractor(get()) }
    single { SpotifyMapper() }
}

fun createOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()

    if(BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
    }

    return builder.build()
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

package ru.ra66it.updaterforspotify.di

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.presentation.viewmodel.SpotifyViewModel
import ru.ra66it.updaterforspotify.sharedPreferencesName

val applicationModule = module {
    single { createSharedPreferences(androidApplication()) }
    viewModel { SpotifyViewModel(get(), get(), get()) }
}

fun createSharedPreferences(androidApplication: Application): SharedPreferencesHelper {
    val sharedPreferences = androidApplication.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    return SharedPreferencesHelper(sharedPreferences)
}


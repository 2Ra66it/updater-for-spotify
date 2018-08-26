package ru.ra66it.updaterforspotify.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.presentation.mvp.presenter.SpotifyPresenter
import javax.inject.Singleton

@Module
class ApplicationModule(context: Context) {

    private val context: Context = context.applicationContext

    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    internal fun provideSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    internal fun providePhotosPresenter(photosInteractor: SpotifyInteractor, queryPreferences: QueryPreferences): SpotifyPresenter {
        return SpotifyPresenter(photosInteractor, queryPreferences)
    }

}

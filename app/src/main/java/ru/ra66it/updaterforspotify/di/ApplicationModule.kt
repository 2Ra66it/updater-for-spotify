package ru.ra66it.updaterforspotify.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ru.ra66it.updaterforspotify.sharedPreferencesName
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
        return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    }

}
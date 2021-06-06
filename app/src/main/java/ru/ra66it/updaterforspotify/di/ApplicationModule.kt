package ru.ra66it.updaterforspotify.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import ru.ra66it.updaterforspotify.sharedPreferencesName
import javax.inject.Singleton

@Module
class ApplicationModule(val context: Context) {

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideWorkersEnqueueManager(): WorkersEnqueueManager {
        return WorkersEnqueueManager(WorkManager.getInstance(context))
    }

}
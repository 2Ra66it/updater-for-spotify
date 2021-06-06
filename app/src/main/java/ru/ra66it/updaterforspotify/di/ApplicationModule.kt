package ru.ra66it.updaterforspotify.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import ru.ra66it.updaterforspotify.sharedPreferencesName
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideWorkersEnqueueManager(@ApplicationContext context: Context): WorkersEnqueueManager {
        return WorkersEnqueueManager(WorkManager.getInstance(context))
    }

}
package ru.ra66it.updaterforspotify

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.ra66it.updaterforspotify.di.applicationModule
import ru.ra66it.updaterforspotify.di.networkModule

/**
 * Created by 2Rabbit on 04.12.2017.
 */

class UpdaterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidLogger()
            androidContext(this@UpdaterApp)
            modules(applicationModule, networkModule)
        }
        instance = this
    }

    companion object {
        lateinit var instance: UpdaterApp
            private set
    }
}

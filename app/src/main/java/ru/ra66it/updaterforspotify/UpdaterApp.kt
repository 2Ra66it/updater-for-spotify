package ru.ra66it.updaterforspotify

import android.app.Application
import ru.ra66it.updaterforspotify.di.ApplicationComponent
import ru.ra66it.updaterforspotify.di.ApplicationModule
import ru.ra66it.updaterforspotify.di.DaggerApplicationComponent
import ru.ra66it.updaterforspotify.di.NetworkModule

/**
 * Created by 2Rabbit on 04.12.2017.
 */

class UpdaterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initComponent()
        instance = this
    }

    private fun initComponent() {
        applicationComponent =  DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .networkModule(NetworkModule())
                .build()
    }

    companion object {
        lateinit var applicationComponent: ApplicationComponent
            private set


        lateinit var instance: UpdaterApp
            private set
    }
}

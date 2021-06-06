package ru.ra66it.updaterforspotify

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UpdaterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: UpdaterApp
            private set
    }
}


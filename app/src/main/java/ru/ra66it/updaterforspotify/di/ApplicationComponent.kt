package ru.ra66it.updaterforspotify.di

import dagger.Component
import ru.ra66it.updaterforspotify.presentation.ui.activity.MainActivity
import ru.ra66it.updaterforspotify.presentation.ui.activity.SettingsActivity
import ru.ra66it.updaterforspotify.presentation.workers.CheckingWorker
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, NetworkModule::class])
interface ApplicationComponent {

    fun inject(activity: SettingsActivity)

    fun inject(activity: MainActivity)

    fun inject(worker: CheckingWorker)
}
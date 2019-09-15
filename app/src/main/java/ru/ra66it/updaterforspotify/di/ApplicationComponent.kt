package ru.ra66it.updaterforspotify.di

import dagger.Component
import ru.ra66it.updaterforspotify.presentation.ui.activity.MainActivity
import ru.ra66it.updaterforspotify.presentation.ui.fragment.SettingsFragment
import ru.ra66it.updaterforspotify.presentation.workers.CheckingWorker
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, NetworkModule::class])
interface ApplicationComponent {

    fun inject(fragment: SettingsFragment)

    fun inject(activity: MainActivity)

    fun inject(worker: CheckingWorker)
}
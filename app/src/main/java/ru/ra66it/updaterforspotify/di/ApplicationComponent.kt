package ru.ra66it.updaterforspotify.di

import dagger.Component
import ru.ra66it.updaterforspotify.presentation.service.PollService
import ru.ra66it.updaterforspotify.presentation.ui.activity.MainActivity
import ru.ra66it.updaterforspotify.presentation.ui.fragment.SettingsFragment
import javax.inject.Singleton

/**
 * Created by 2Rabbit on 04.12.2017.
 */

@Singleton
@Component(modules = [ApplicationModule::class, NetworkModule::class])
interface ApplicationComponent {

    fun inject(fragment: SettingsFragment)

    fun inject(activity: MainActivity)

    fun inject(service: PollService)

}

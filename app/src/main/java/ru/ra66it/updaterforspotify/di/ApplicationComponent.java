package ru.ra66it.updaterforspotify.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.ra66it.updaterforspotify.presentation.service.PollService;
import ru.ra66it.updaterforspotify.presentation.ui.activity.IntroActivity;
import ru.ra66it.updaterforspotify.presentation.ui.activity.MainActivity;
import ru.ra66it.updaterforspotify.presentation.ui.fragment.SettingsFragment;


/**
 * Created by 2Rabbit on 04.12.2017.
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class, PresenterModule.class})
public interface ApplicationComponent {

    void inject(SettingsFragment fragment);

    void inject(IntroActivity activity);

    void inject(MainActivity activity);

    void inject(PollService service);

}

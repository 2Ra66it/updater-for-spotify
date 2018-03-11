package ru.ra66it.updaterforspotify.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.ra66it.updaterforspotify.ui.fragment.SpotifyFragment;
import ru.ra66it.updaterforspotify.notification.PollService;
import ru.ra66it.updaterforspotify.ui.activity.IntroActivity;
import ru.ra66it.updaterforspotify.ui.activity.MainActivity;
import ru.ra66it.updaterforspotify.ui.fragment.SettingsFragment;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

@Singleton
@Component(modules = {RestModule.class, SharedPreferencesModule.class})
public interface ApplicationComponent {

    void inject(SpotifyFragment fragment);

    void inject(SettingsFragment fragment);

    void inject(IntroActivity activity);

    void inject(MainActivity activity);

    void inject(PollService service);
}

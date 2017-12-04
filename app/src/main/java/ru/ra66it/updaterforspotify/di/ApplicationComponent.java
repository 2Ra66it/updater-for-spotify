package ru.ra66it.updaterforspotify.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.ra66it.updaterforspotify.notification.PollService;
import ru.ra66it.updaterforspotify.ui.activity.MainActivity;
import ru.ra66it.updaterforspotify.ui.fragment.SpotifyDfFragment;
import ru.ra66it.updaterforspotify.ui.fragment.SpotifyOriginFragment;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

@Singleton
@Component(modules = RestModule.class)
public interface ApplicationComponent {

    void inject(SpotifyDfFragment fragment);
    void inject(SpotifyOriginFragment fragment);

    void inject(PollService service);
}

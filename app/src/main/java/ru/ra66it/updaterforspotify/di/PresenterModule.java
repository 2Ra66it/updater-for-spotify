package ru.ra66it.updaterforspotify.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences;
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor;
import ru.ra66it.updaterforspotify.presentation.mvp.presenter.IntroActivityPresenter;
import ru.ra66it.updaterforspotify.presentation.mvp.presenter.MainActivityPresenter;
import ru.ra66it.updaterforspotify.presentation.mvp.presenter.SpotifyPresenter;

@Module
public class PresenterModule {

    @Provides
    @Singleton
    SpotifyPresenter providePhotosPresenter(SpotifyInteractor photosInteractor) {
        return new SpotifyPresenter(photosInteractor);
    }

    @Provides
    @Singleton
    MainActivityPresenter provideMainActivityPresenter(QueryPreferences queryPreferences) {
        return new MainActivityPresenter(queryPreferences);
    }

    @Provides
    @Singleton
    IntroActivityPresenter provideIntroActivityPresenter(QueryPreferences queryPreferences) {
        return new IntroActivityPresenter(queryPreferences);
    }
}

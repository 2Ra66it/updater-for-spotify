package ru.ra66it.updaterforspotify;

import android.app.Application;

import ru.ra66it.updaterforspotify.di.ApplicationComponent;
import ru.ra66it.updaterforspotify.di.DaggerApplicationComponent;
import ru.ra66it.updaterforspotify.di.RestModule;

/**
 * Created by 2Rabbit on 04.12.2017.
 */

public class MyApplication extends Application {

    private static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initComponent();
    }

    private void initComponent() {
        applicationComponent = DaggerApplicationComponent.builder().restModule(new RestModule()).build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

}

package ru.ra66it.updaterforspotify.di;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by 2Rabbit on 08.03.2018.
 */
@Module
public class SharedPreferencesModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
    }
}

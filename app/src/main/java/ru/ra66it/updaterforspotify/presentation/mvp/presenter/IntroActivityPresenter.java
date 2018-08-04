package ru.ra66it.updaterforspotify.presentation.mvp.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import ru.ra66it.updaterforspotify.UpdaterApp;
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences;
import ru.ra66it.updaterforspotify.presentation.mvp.view.IntroView;

/**
 * Created by 2Rabbit on 08.03.2018.
 */

public class IntroActivityPresenter {

    private IntroView mView;
    private QueryPreferences queryPreferences;

    public IntroActivityPresenter(QueryPreferences queryPreferences) {
        this.queryPreferences = queryPreferences;
    }

    public void setView(IntroView mView) {
        this.mView = mView;
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(UpdaterApp.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            queryPreferences.setFirstLaunch(false);
            mView.finish();
        } else {
            mView.getPermission();
        }
    }

}

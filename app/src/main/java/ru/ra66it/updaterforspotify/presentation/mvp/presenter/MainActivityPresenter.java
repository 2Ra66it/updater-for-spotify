package ru.ra66it.updaterforspotify.presentation.mvp.presenter;


import ru.ra66it.updaterforspotify.data.storage.QueryPreferences;
import ru.ra66it.updaterforspotify.presentation.mvp.view.MainBaseView;
import ru.ra66it.updaterforspotify.presentation.service.PollService;


/**
 * Created by 2Rabbit on 12.11.2017.
 */

public class MainActivityPresenter {

    private MainBaseView mView;
    private QueryPreferences queryPreferences;

    public MainActivityPresenter(QueryPreferences queryPreferences) {
        this.queryPreferences = queryPreferences;
    }

    public void setView(MainBaseView mView) {
        this.mView = mView;
        if (queryPreferences.isFirstLaunch()) {
            mView.startIntroActivity();
        }
    }

    public void startNotification() {
        if (queryPreferences.isEnableNotification()) {
            PollService.setServiceAlarm(queryPreferences.isEnableNotification());
        } else {
            PollService.setServiceAlarm(false);
        }
    }

}

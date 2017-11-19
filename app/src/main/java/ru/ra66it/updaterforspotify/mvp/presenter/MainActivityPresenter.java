package ru.ra66it.updaterforspotify.mvp.presenter;


import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.ra66it.updaterforspotify.QueryPreferneces;
import ru.ra66it.updaterforspotify.mvp.view.MainBaseView;
import ru.ra66it.updaterforspotify.notification.PollService;


/**
 * Created by 2Rabbit on 12.11.2017.
 */

@InjectViewState
public class MainActivityPresenter extends MvpPresenter<MainBaseView> {

    public MainActivityPresenter() {

    }

    public void initViewPager() {
        getViewState().initViewPager();
    }


    public void startIntro(Context context) {
        if (QueryPreferneces.isFirstLaunch(context)) {
            getViewState().startIntroActivity();
        }
    }

    public void startNotification(Context context) {
        if (QueryPreferneces.getNotificationDogFood(context)) {
            PollService.setServiceAlarm(context,
                    QueryPreferneces.getNotificationDogFood(context));
        } else if (QueryPreferneces.getNotificationOrigin(context)) {
            PollService.setServiceAlarm(context,
                    QueryPreferneces.getNotificationOrigin(context));
        }
    }


}

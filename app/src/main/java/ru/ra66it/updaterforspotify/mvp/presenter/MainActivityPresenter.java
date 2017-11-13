package ru.ra66it.updaterforspotify.mvp.presenter;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.ra66it.updaterforspotify.mvp.view.MainBaseView;


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

    public void getPermission() {
        getViewState().getPermission();
    }



}

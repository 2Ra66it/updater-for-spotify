package ru.ra66it.updaterforspotify.utils;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import static android.view.View.GONE;

/**
 * Created by 2Rabbit on 23.09.2017.
 */

public class UtilsFAB {

    public static void hideOrShowFAB(FloatingActionButton fab, boolean hide) {

        if (hide) {
            fab.setVisibility(GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

    }
}

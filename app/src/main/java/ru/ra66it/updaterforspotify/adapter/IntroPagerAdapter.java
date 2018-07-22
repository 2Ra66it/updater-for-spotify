package ru.ra66it.updaterforspotify.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.ra66it.updaterforspotify.ui.fragment.intro.IntroChooseFragment;
import ru.ra66it.updaterforspotify.ui.fragment.intro.IntroHelloFragment;
import ru.ra66it.updaterforspotify.ui.fragment.intro.IntroPermissionFragment;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroPagerAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 3;

    public IntroPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return IntroHelloFragment.newInstance();
            case 1:
                return IntroChooseFragment.newInstance();
            case 2:
                return IntroPermissionFragment.newInstance();
        }
        return null;
    }


    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

}

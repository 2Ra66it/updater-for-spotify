package ru.ra66it.updaterforspotify.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.ra66it.updaterforspotify.ui.fragment.SpotifyDfFragment;
import ru.ra66it.updaterforspotify.ui.fragment.SpotifyOriginFragment;


/**
 * Created by 2Rabbit on 06.11.2017.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {


    private static final int FRAGMENT_COUNT = 2;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return SpotifyDfFragment.newInstance();
            case 1:
                return SpotifyOriginFragment.newInstance();

        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Spotify Dogfood";
            case 1:
                return "Spotify";
        }
        return null;
    }

}
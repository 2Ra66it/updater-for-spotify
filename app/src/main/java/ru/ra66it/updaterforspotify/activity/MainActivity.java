package ru.ra66it.updaterforspotify.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.ra66it.updaterforspotify.QueryPreferneces;
import ru.ra66it.updaterforspotify.fragment.ChooseNotificationDialog;
import ru.ra66it.updaterforspotify.fragment.SpotifyDfFragment;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.fragment.SpotifyOrigFragment;
import ru.ra66it.updaterforspotify.notification.PollService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    private TabLayout tabLayout;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public class PagerAdapter extends FragmentPagerAdapter {


        private static final int FRAGMENT_COUNT = 2;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return SpotifyDfFragment.newInstance();
                case 1:
                    return SpotifyOrigFragment.newInstance();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (QueryPreferneces.getNotificationDogFood(this)) {
            PollService.setServiceAlarm(this,
                    QueryPreferneces.getNotificationDogFood(this));
        } else if (QueryPreferneces.getNotificationOrigin(this)) {
            PollService.setServiceAlarm(this,
                    QueryPreferneces.getNotificationOrigin(this));
        }

    }
}
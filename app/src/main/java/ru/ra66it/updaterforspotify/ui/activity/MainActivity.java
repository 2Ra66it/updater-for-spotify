package ru.ra66it.updaterforspotify.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.mvp.presenter.MainActivityPresenter;
import ru.ra66it.updaterforspotify.mvp.view.MainBaseView;
import ru.ra66it.updaterforspotify.storage.QueryPreferences;
import ru.ra66it.updaterforspotify.ui.fragment.SpotifyFragment;
import ru.ra66it.updaterforspotify.utils.ActivityUtils;


public class MainActivity extends AppCompatActivity implements MainBaseView {

    private MainActivityPresenter mPresenter;

    @Inject
    QueryPreferences queryPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        MyApplication.getApplicationComponent().inject(this);
        mPresenter = new MainActivityPresenter(this, queryPreferences);

        SpotifyFragment spotifyFragmentFragment = SpotifyFragment.newInstance();
        ActivityUtils.addSupportFragmentToActivity(getSupportFragmentManager(),
                spotifyFragmentFragment, R.id.fragment_container);

        mPresenter.startIntroActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.startNotification();
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


    @Override
    public void startIntroActivity() {
        startActivity(new Intent(this, IntroActivity.class));
    }


}

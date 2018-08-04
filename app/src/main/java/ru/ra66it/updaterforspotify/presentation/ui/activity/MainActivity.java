package ru.ra66it.updaterforspotify.presentation.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import ru.ra66it.updaterforspotify.UpdaterApp;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.presentation.mvp.presenter.MainActivityPresenter;
import ru.ra66it.updaterforspotify.presentation.mvp.view.MainBaseView;
import ru.ra66it.updaterforspotify.presentation.ui.fragment.SpotifyFragment;
import ru.ra66it.updaterforspotify.utils.ActivityUtils;


public class MainActivity extends AppCompatActivity implements MainBaseView {

    @Inject
    MainActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        UpdaterApp.getApplicationComponent().inject(this);

        mPresenter.setView(this);

        SpotifyFragment spotifyFragmentFragment = SpotifyFragment.newInstance();
        ActivityUtils.addSupportFragmentToActivity(getSupportFragmentManager(),
                spotifyFragmentFragment, R.id.fragment_container);
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

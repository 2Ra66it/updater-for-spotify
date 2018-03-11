package ru.ra66it.updaterforspotify.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.ra66it.updaterforspotify.ui.fragment.SettingsFragment;
import ru.ra66it.updaterforspotify.utils.ActivityUtils;


/**
 * Created by 2Rabbit on 25.09.2017.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SettingsFragment settingsFragment = SettingsFragment.newInstance();
        ActivityUtils.addFragmentToActivity(getFragmentManager(),
                settingsFragment, android.R.id.content);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

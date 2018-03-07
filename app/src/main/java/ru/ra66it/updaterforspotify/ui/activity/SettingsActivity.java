package ru.ra66it.updaterforspotify.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.ra66it.updaterforspotify.ui.fragment.SettingsFragment;


/**
 * Created by 2Rabbit on 25.09.2017.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        SettingsFragment settingsFragment = new SettingsFragment();
        ft.add(android.R.id.content, settingsFragment, "SettingsFragment");
        ft.commit();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

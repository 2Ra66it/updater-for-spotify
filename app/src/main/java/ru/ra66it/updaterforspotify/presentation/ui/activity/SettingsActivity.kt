package ru.ra66it.updaterforspotify.presentation.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import ru.ra66it.updaterforspotify.presentation.ui.fragment.SettingsFragment
import ru.ra66it.updaterforspotify.presentation.utils.ActivityUtils


/**
 * Created by 2Rabbit on 25.09.2017.
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val settingsFragment = SettingsFragment.newInstance()
        ActivityUtils.addFragmentToActivity(fragmentManager,
                settingsFragment, android.R.id.content)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

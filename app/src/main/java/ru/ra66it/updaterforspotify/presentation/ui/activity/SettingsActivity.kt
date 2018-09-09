package ru.ra66it.updaterforspotify.presentation.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.ra66it.updaterforspotify.presentation.ui.fragment.SettingsFragment


/**
 * Created by 2Rabbit on 25.09.2017.
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        val transaction = fragmentManager.beginTransaction()
        transaction.add(android.R.id.content, SettingsFragment.newInstance())
        transaction.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

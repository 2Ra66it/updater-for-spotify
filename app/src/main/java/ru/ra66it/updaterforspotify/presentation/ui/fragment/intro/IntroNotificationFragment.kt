package ru.ra66it.updaterforspotify.presentation.ui.fragment.intro

import agency.tango.materialintroscreen.SlideFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.intro_choose_fragment.*
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences

/**
 * Created by 2Rabbit on 17.11.2017.
 */

class IntroNotificationFragment : SlideFragment() {

    private lateinit var queryPreferences: QueryPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.intro_choose_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switch_notif.setOnCheckedChangeListener { _, _ ->  queryPreferences.isEnableNotification = switch_notif.isChecked}
    }

    fun setSharedPreferences(queryPreferences: QueryPreferences) {
        this.queryPreferences = queryPreferences
    }

    override fun backgroundColor(): Int {
        return R.color.colorPrimaryDark
    }

    override fun buttonsColor(): Int {
        return R.color.colorAccent
    }
}

package ru.ra66it.updaterforspotify.presentation.ui.activity

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.os.Bundle
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences
import ru.ra66it.updaterforspotify.presentation.ui.fragment.intro.IntroNotificationFragment
import javax.inject.Inject

/**
 * Created by 2Rabbit on 17.11.2017.
 */

class IntroActivity : MaterialIntroActivity() {

    @Inject lateinit var queryPreferences: QueryPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdaterApp.applicationComponent.inject(this)
        enableLastSlideAlphaExitTransition(true)

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.colorAccent)
                .title(getString(R.string.intro_welcome_message))
                .build())

        val notificationFragment = IntroNotificationFragment()
        notificationFragment.setSharedPreferences(queryPreferences)
        addSlide(notificationFragment)

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.colorAccent)
                .neededPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .image(R.drawable.art_material_metaphor)
                .description(getString(R.string.intro_permission_text))
                .build(),
                MessageButtonBehaviour({ showMessage("Permission Granted") }, "Granted"))
    }

    override fun onFinish() {
        super.onFinish()
        queryPreferences.isFirstLaunch = false
    }
}

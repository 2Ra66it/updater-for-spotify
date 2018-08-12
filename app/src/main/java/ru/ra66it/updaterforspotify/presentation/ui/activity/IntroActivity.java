package ru.ra66it.updaterforspotify.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.UpdaterApp;
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences;
import ru.ra66it.updaterforspotify.presentation.ui.fragment.intro.IntroNotificationFragment;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroActivity extends MaterialIntroActivity {

    @Inject
    QueryPreferences queryPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UpdaterApp.getApplicationComponent().inject(this);
        enableLastSlideAlphaExitTransition(true);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimaryDark)
                        .buttonsColor(R.color.colorAccent)
                        .title(getString(R.string.intro_welcome_message))
                        .build());

        IntroNotificationFragment notificationFragment = new IntroNotificationFragment();
        notificationFragment.setSharedPreferences(queryPreferences);
        addSlide(notificationFragment);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimaryDark)
                        .buttonsColor(R.color.colorAccent)
                        .neededPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
                        .image(R.drawable.art_material_metaphor)
                        .description(getString(R.string.intro_permission_text))
                        .build(),
                new MessageButtonBehaviour(v -> showMessage("Permission Granted"), "Granted"));
    }

    @Override
    public void onFinish() {
        super.onFinish();
        queryPreferences.setFirstLaunch(false);
    }
}

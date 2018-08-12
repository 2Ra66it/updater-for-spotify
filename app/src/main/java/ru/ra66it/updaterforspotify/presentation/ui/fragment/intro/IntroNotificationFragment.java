package ru.ra66it.updaterforspotify.presentation.ui.fragment.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import javax.inject.Inject;

import agency.tango.materialintroscreen.SlideFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.UpdaterApp;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.data.storage.QueryPreferences;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroNotificationFragment extends SlideFragment {

    @BindView(R.id.switch_notif)
    Switch switchNotification;

    QueryPreferences queryPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_choose_fragment, container, false);
        ButterKnife.bind(this, v);

        switchNotification.setOnCheckedChangeListener((compoundButton, b) -> queryPreferences.setEnableNotification(switchNotification.isChecked()));

        return v;
    }

    public void setSharedPreferences(QueryPreferences queryPreferences) {
        this.queryPreferences = queryPreferences;
    }

    @Override
    public int backgroundColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorAccent;
    }
}

package ru.ra66it.updaterforspotify.ui.fragment.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.storage.QueryPreferences;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroChooseFragment extends Fragment {

    @BindView(R.id.switch_notif)
    Switch switchNotification;

    @Inject
    QueryPreferences queryPreferences;

    public static IntroChooseFragment newInstance() {
        return new IntroChooseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_choose_fragment, container, false);
        ButterKnife.bind(this, v);
        MyApplication.getApplicationComponent().inject(this);

        switchNotification.setOnCheckedChangeListener((compoundButton, b) -> queryPreferences.setNotifications(switchNotification.isChecked()));

        return v;
    }
}

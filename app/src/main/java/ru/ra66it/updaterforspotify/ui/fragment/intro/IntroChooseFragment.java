package ru.ra66it.updaterforspotify.ui.fragment.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.R;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroChooseFragment extends Fragment {

    @BindView(R.id.layout_choose_sptf)
    LinearLayout llChooseOrigin;
    @BindView(R.id.layout_choose_sptf_beta)
    LinearLayout llChooseBeta;
    @BindView(R.id.layout_choose_nothing)
    LinearLayout llChooseNothin;

    public static IntroChooseFragment newInstance() {
        return new IntroChooseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_choose_fragment, container, false);
        ButterKnife.bind(this, v);

        llChooseOrigin.setOnClickListener(view -> {

            llChooseOrigin.setBackgroundColor(getResources().getColor(R.color.darkChoose));
            llChooseBeta.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llChooseNothin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

           /* QueryPreferences.setNotificationOrigin(getContext(), true);
            QueryPreferences.setSpotifyBeta(getContext(), false);*/
        });

        llChooseBeta.setOnClickListener(view -> {

            llChooseBeta.setBackgroundColor(getResources().getColor(R.color.darkChoose));
            llChooseOrigin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llChooseNothin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            /*QueryPreferences.setNotificationOrigin(getContext(), false);
            QueryPreferences.setSpotifyBeta(getContext(), true);*/
        });

        llChooseNothin.setBackgroundColor(getResources().getColor(R.color.darkChoose));
        llChooseNothin.setOnClickListener(view -> {

            llChooseNothin.setBackgroundColor(getResources().getColor(R.color.darkChoose));
            llChooseBeta.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llChooseOrigin.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            /*QueryPreferences.setNotificationOrigin(getContext(), false);
            QueryPreferences.setSpotifyBeta(getContext(), false);*/
        });

        return v;
    }
}

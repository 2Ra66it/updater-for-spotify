package ru.ra66it.updaterforspotify.ui.fragment.intro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.ra66it.updaterforspotify.R;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroChooseFragment extends Fragment {

    @BindViews({R.id.layout_choose_sptf, R.id.layout_choose_sptf_beta, R.id.layout_choose_nothing})
    List<LinearLayout> choices;

    private Unbinder unbinder = Unbinder.EMPTY;

    public static IntroChooseFragment newInstance() {
        return new IntroChooseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_choose_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.layout_choose_sptf)
    void onSpotifyOriginChoose(View view) {
        ButterKnife.apply(choices, new ChooseAction(view.getId()));
        /* QueryPreferences.setNotificationOrigin(getContext(), true);
            QueryPreferences.setSpotifyBeta(getContext(), false);*/
    }

    @OnClick(R.id.layout_choose_sptf_beta)
    void onSpotifyBetaChoose(View view) {
        ButterKnife.apply(choices, new ChooseAction(view.getId()));
        /*QueryPreferences.setNotificationOrigin(getContext(), false);
            QueryPreferences.setSpotifyBeta(getContext(), true);*/
    }

    @OnClick(R.id.layout_choose_nothing)
    void onNothingChoose(View view) {
        ButterKnife.apply(choices, new ChooseAction(view.getId()));
        /*QueryPreferences.setNotificationOrigin(getContext(), false);
            QueryPreferences.setSpotifyBeta(getContext(), false);*/
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    private final class ChooseAction implements ButterKnife.Action<LinearLayout> {

        private final int viewId;

        ChooseAction(int viewId) {
            this.viewId = viewId;
        }

        @Override
        public void apply(@NonNull LinearLayout view, int index) {
            if (view.getId() == viewId) {
                view.setBackgroundColor(getResources().getColor(R.color.darkChoose));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
    }
}

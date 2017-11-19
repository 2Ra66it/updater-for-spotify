package ru.ra66it.updaterforspotify.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.mvp.presenter.SpotifyOriginPresenter;
import ru.ra66it.updaterforspotify.mvp.view.BaseViewFragment;
import ru.ra66it.updaterforspotify.notification.VisibleFragment;


import static android.view.View.GONE;

/**
 * Created by 2Rabbit on 12.11.2017.
 */

public class SpotifyOriginFragment extends VisibleFragment implements BaseViewFragment {

    @InjectPresenter
    SpotifyOriginPresenter mPresenter;
    @BindView(R.id.cv_latest_orig)
    CardView cvLatestOrig;
    @BindView(R.id.lbl_latest_version_orig)
    TextView lblLatestVersion;
    @BindView(R.id.lbl_install_version_orig)
    TextView lblInstallVersion;
    @BindView(R.id.latest_progress_bar_orig)
    ProgressBar progressBar;
    @BindView(R.id.swipeContainer_orig)
    SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.fab_orig)
    FloatingActionButton fabDownloadButton;
    @BindView(R.id.layout_cards_orig)
    LinearLayout layoutCards;

    public static SpotifyOriginFragment newInstance() {
        return new SpotifyOriginFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getLatestVersionSpotify(getContext());
    }



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spotify_orig_fragment, container, false);
        ButterKnife.bind(this, v);


        swipeToRefresh.setOnRefreshListener(() -> {
            swipeToRefresh.setRefreshing(false);
            mPresenter.getLatestVersionSpotify(getContext());
        });


        fabDownloadButton.setOnClickListener(view -> mPresenter.downloadLatestVersion(getContext()));

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkInstalledSpotifyVersion(getContext(), fabDownloadButton);
    }

    @Override
    public void showCardProgress() {
        lblLatestVersion.setVisibility(GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCardProgress() {
        progressBar.setVisibility(View.GONE);
        lblLatestVersion.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorSnackbar(int stringId) {
        Snackbar.make(swipeToRefresh, getString(stringId), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLatestVersion(String version) {
        lblLatestVersion.setText(version);
    }

    @Override
    public void hideCardView() {
        cvLatestOrig.setVisibility(View.GONE);
    }

    @Override
    public void showCardView() {
        cvLatestOrig.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFAB() {
        fabDownloadButton.setVisibility(View.GONE);
    }

    @Override
    public void showFAB() {
        fabDownloadButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setInstalledVersion(String installVersion) {
        lblInstallVersion.setText(installVersion);
    }

    @Override
    public void showLayoutCards() {
        layoutCards.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLayoutCards() {
        layoutCards.setVisibility(View.GONE);
    }


    @Override
    public void setLatestVersionAvailable(String latestVersionAvailable) {
        lblLatestVersion.setText(latestVersionAvailable);
    }
}

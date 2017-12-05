package ru.ra66it.updaterforspotify.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.mvp.presenter.SpotifyDfPresenter;
import ru.ra66it.updaterforspotify.mvp.view.BaseViewFragment;
import ru.ra66it.updaterforspotify.notification.VisibleFragment;
import ru.ra66it.updaterforspotify.rest.SpotifyApi;


import static android.view.View.GONE;

/**
 * Created by 2Rabbit on 09.11.2017.
 */

public class SpotifyDfFragment extends VisibleFragment implements BaseViewFragment {

    @BindView(R.id.cv_latest_df)
    CardView cvLatestDf;
    @BindView(R.id.lbl_latest_version)
    TextView lblLatestVersion;
    @BindView(R.id.lbl_install_version)
    TextView lblInstallVersion;
    @BindView(R.id.latest_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.fab)
    FloatingActionButton fabDownloadButton;
    @BindView(R.id.layout_cards)
    LinearLayout layoutCards;

    private SpotifyDfPresenter mPresenter;

    @Inject
    SpotifyApi spotifyApi;


    public static SpotifyDfFragment newInstance() {
        return new SpotifyDfFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getApplicationComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spotify_df_fragment, container, false);
        ButterKnife.bind(this, v);
        mPresenter = new SpotifyDfPresenter(this, spotifyApi);

        swipeToRefresh.setOnRefreshListener(() -> {
            swipeToRefresh.setRefreshing(false);
            mPresenter.getLatestVersionDf(getContext());
        });

        fabDownloadButton.setOnClickListener(view -> mPresenter.downloadLatestVersion(getContext()));

        mPresenter.getLatestVersionDf(getContext());

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkInstalledSpotifyDfVersion(getContext(), fabDownloadButton);
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
        Snackbar.make(getActivity().findViewById(android.R.id.content), getString(stringId), Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void hideCardView() {
        cvLatestDf.setVisibility(View.GONE);
    }

    @Override
    public void showCardView() {
        cvLatestDf.setVisibility(View.VISIBLE);
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

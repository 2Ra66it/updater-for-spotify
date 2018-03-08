package ru.ra66it.updaterforspotify.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.ra66it.updaterforspotify.MyApplication;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.mvp.presenter.SpotifyOriginPresenter;
import ru.ra66it.updaterforspotify.mvp.view.BaseViewFragment;
import ru.ra66it.updaterforspotify.rest.SpotifyApi;
import ru.ra66it.updaterforspotify.storage.QueryPreferences;

import static android.view.View.GONE;

/**
 * Created by 2Rabbit on 12.11.2017.
 */

public class SpotifyOriginFragment extends Fragment implements BaseViewFragment {

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
    @BindView(R.id.layout_no_internet_orig)
    RelativeLayout noInternetOrigLayout;

    private SpotifyOriginPresenter mPresenter;
    private Unbinder unbinder;

    @Inject
    SpotifyApi spotifyApi;
    @Inject
    QueryPreferences queryPreferences;

    public static SpotifyOriginFragment newInstance() {
        return new SpotifyOriginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getApplicationComponent().inject(this);
        mPresenter = new SpotifyOriginPresenter(queryPreferences, this, spotifyApi);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spotify_orig_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);

        swipeToRefresh.setOnRefreshListener(() -> {
            swipeToRefresh.setRefreshing(false);
            mPresenter.getLatestVersionSpotify();
        });


        fabDownloadButton.setOnClickListener(view -> mPresenter.downloadLatestVersion());

        mPresenter.getLatestVersionSpotify();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkInstalledSpotifyVersion();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDispose();
        unbinder.unbind();
    }

    @Override
    public void showProgress() {
        layoutCards.setVisibility(GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showErrorSnackbar(int stringId) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), getString(stringId), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNoInternetLayout() {
        noInternetOrigLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoInternetLayout() {
        noInternetOrigLayout.setVisibility(View.GONE);
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
    public void setUpdateImageFAB() {
        fabDownloadButton.setImageResource(R.drawable.ic_autorenew_black_24dp);
    }

    @Override
    public void setInstallImageFAB() {
        fabDownloadButton.setImageResource(R.drawable.ic_file_download_black_24dp);
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

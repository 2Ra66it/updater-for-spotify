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

import com.arellomobile.mvp.presenter.InjectPresenter;


import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.mvp.presenter.SpotifyDfPresenter;
import ru.ra66it.updaterforspotify.mvp.view.BaseViewFragment;
import ru.ra66it.updaterforspotify.notification.VisibleFragment;


import static android.view.View.GONE;

/**
 * Created by 2Rabbit on 09.11.2017.
 */

public class SpotifyDfFragment extends VisibleFragment implements BaseViewFragment {

    @InjectPresenter
    SpotifyDfPresenter mPresenter;
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


    public static SpotifyDfFragment newInstance() {
        return new SpotifyDfFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getLatestVersionDf(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spotify_df_fragment, container, false);
        ButterKnife.bind(this, v);


        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToRefresh.setRefreshing(false);
                mPresenter.getLatestVersionDf(getContext());
            }
        });

        fabDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.downloadLatestVersion(getContext());
            }
        });



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
        Snackbar.make(swipeToRefresh, getString(stringId), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLatestVersion(String version) {
        lblLatestVersion.setText(version);
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

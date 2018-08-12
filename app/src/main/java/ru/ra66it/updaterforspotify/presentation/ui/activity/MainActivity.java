package ru.ra66it.updaterforspotify.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.UpdaterApp;
import ru.ra66it.updaterforspotify.presentation.mvp.presenter.SpotifyPresenter;
import ru.ra66it.updaterforspotify.presentation.mvp.view.SpotifyView;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements SpotifyView {

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

    @Inject
    SpotifyPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UpdaterApp.getApplicationComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter.setView(this);

        swipeToRefresh.setOnRefreshListener(() -> {
            swipeToRefresh.setRefreshing(false);
            mPresenter.getLatestVersionSpotify();
        });

        fabDownloadButton.setOnClickListener(view -> mPresenter.downloadLatestVersion());

        mPresenter.showIntro();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
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
        Snackbar.make(findViewById(android.R.id.content), getString(stringId), Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void showIntro() {
        startActivity(new Intent(this, IntroActivity.class));
    }
}

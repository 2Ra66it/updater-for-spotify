package ru.ra66it.updaterforspotify.mvp.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.model.FullSpotifyModel;
import ru.ra66it.updaterforspotify.mvp.view.SpotifyView;
import ru.ra66it.updaterforspotify.rest.SpotifyApi;
import ru.ra66it.updaterforspotify.storage.QueryPreferences;
import ru.ra66it.updaterforspotify.utils.StringService;
import ru.ra66it.updaterforspotify.utils.UtilsDownloadSpotify;
import ru.ra66it.updaterforspotify.utils.UtilsNetwork;
import ru.ra66it.updaterforspotify.utils.UtilsSpotify;


/**
 * Created by 2Rabbit on 11.11.2017.
 */

public class SpotifyOriginPresenter {

    private SpotifyView mView;
    private FullSpotifyModel fullSpotifyModel;
    private SpotifyApi spotifyApi;
    private CompositeDisposable compositeDisposable;

    public SpotifyOriginPresenter(SpotifyView mView, SpotifyApi spotifyApi) {
        this.mView = mView;
        this.spotifyApi = spotifyApi;
        this.compositeDisposable = new CompositeDisposable();
    }

    public void getLatestVersionSpotify() {
        if (UtilsNetwork.isNetworkAvailable()) {
            loadData();
        } else {
            errorLayout(true);
            mView.showNoInternetLayout();
        }
    }

    private void loadData() {
        compositeDisposable.add(spotifyApi.getLatestOrigin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> {
                    compositeDisposable.add(d);
                    errorLayout(false);
                    mView.hideNoInternetLayout();
                    mView.showProgress();
                })
                .doOnComplete(() -> {
                    fillData();
                    mView.hideProgress();
                    mView.showLayoutCards();
                })
                .subscribe(spotify -> {
                    fullSpotifyModel = new FullSpotifyModel(spotify);
                }, throwable -> {
                    errorLayout(true);
                    mView.hideProgress();
                    mView.showErrorSnackbar(R.string.error);
                }));
    }

    public void downloadLatestVersion() {
        UtilsDownloadSpotify.downloadSpotify(fullSpotifyModel.getLatestLink(), fullSpotifyModel.getLatestVersionName());
    }

    private void fillData() {
        mView.setLatestVersionAvailable(fullSpotifyModel.getLatestVersionName());
        if (UtilsSpotify.isSpotifyInstalled() &&
                UtilsSpotify.isSpotifyUpdateAvailable(UtilsSpotify.getInstalledSpotifyVersion(), fullSpotifyModel.getLatestVersionNumber())) {
            // Install new version
            mView.showCardView();
            mView.showFAB();
            mView.setUpdateImageFAB();
            mView.setInstalledVersion(UtilsSpotify.getInstalledSpotifyVersion());
        } else if (!UtilsSpotify.isSpotifyInstalled()) {
            // Install spotify now
            mView.setInstallImageFAB();
            mView.showFAB();
            mView.showCardView();
            mView.setInstalledVersion(StringService.getById(R.string.spotify_not_installed));
        } else {
            //have latest version
            mView.hideFAB();
            mView.hideCardView();
            mView.setInstalledVersion(StringService.getById(R.string.up_to_date));
        }
    }

    private void errorLayout(boolean bool) {
        if (bool) {
            mView.hideLayoutCards();
            mView.hideFAB();
        } else {
            mView.showLayoutCards();
            mView.hideFAB();
        }
    }

    public void subscribe() {
        getLatestVersionSpotify();
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }
}

package ru.ra66it.updaterforspotify.mvp.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.model.FullSpotifyModel;
import ru.ra66it.updaterforspotify.mvp.view.BaseViewFragment;
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

    private BaseViewFragment mView;
    private boolean hasError = false;
    private FullSpotifyModel fullSpotifyModel;
    private SpotifyApi spotifyApi;
    private QueryPreferences queryPreferences;
    private CompositeDisposable compositeDisposable;

    public SpotifyOriginPresenter(QueryPreferences queryPreferences, BaseViewFragment mView, SpotifyApi spotifyApi) {
        this.mView = mView;
        this.spotifyApi = spotifyApi;
        this.queryPreferences = queryPreferences;
        this.compositeDisposable = new CompositeDisposable();
    }

    public void getLatestVersionSpotify() {
        if (UtilsNetwork.isNetworkAvailable()) {
            if (!queryPreferences.isSpotifyBeta()) {
                loadDataOrigin();
            } else {
                loadDataBeta();
            }
        } else {
            errorLayout(true);
            mView.showNoInternetLayout();
        }
    }

    private void loadDataBeta() {
        compositeDisposable.add(spotifyApi.getLatestOriginBeta()
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

    private void loadDataOrigin() {
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
            mView.showFAB();
            mView.showCardView();
        } else if (!UtilsSpotify.isSpotifyInstalled()) {
            // Install spotify now
            mView.showFAB();
            mView.showCardView();
        } else {
            //have latest version
            mView.hideFAB();
            mView.hideCardView();
            mView.setInstalledVersion(StringService.getById(R.string.up_to_date));
        }
    }

    public void checkInstalledSpotifyVersion() {
        if (UtilsNetwork.isNetworkAvailable())
            mView.showCardView();
        if (UtilsSpotify.isSpotifyInstalled()) {
            mView.setInstalledVersion(UtilsSpotify.getInstalledSpotifyVersion());
            mView.setUpdateImageFAB();
            getLatestVersionSpotify();
        } else {
            mView.showFAB();
            mView.setInstallImageFAB();
            mView.setInstalledVersion(StringService.getById(R.string.spotify_not_installed));
            if (hasError) {
                mView.hideFAB();
            }
        }
    }

    private void errorLayout(boolean bool) {
        if (bool) {
            hasError = true;
            mView.hideLayoutCards();
            mView.hideFAB();
        } else {
            hasError = false;
            mView.showLayoutCards();
            mView.hideFAB();
        }
    }

    public void onDispose() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}

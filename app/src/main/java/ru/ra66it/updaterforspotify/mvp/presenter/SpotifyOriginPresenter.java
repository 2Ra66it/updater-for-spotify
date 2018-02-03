package ru.ra66it.updaterforspotify.mvp.presenter;

import android.content.Context;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.model.FullSpotifyModel;
import ru.ra66it.updaterforspotify.mvp.view.BaseViewFragment;
import ru.ra66it.updaterforspotify.rest.SpotifyApi;
import ru.ra66it.updaterforspotify.storage.QueryPreferneces;
import ru.ra66it.updaterforspotify.utils.UtilsDownloadSpotify;
import ru.ra66it.updaterforspotify.utils.UtilsNetwork;
import ru.ra66it.updaterforspotify.utils.UtilsSpotify;


/**
 * Created by 2Rabbit on 11.11.2017.
 */

public class SpotifyOriginPresenter {

    private BaseViewFragment view;
    private boolean hasError = false;
    private Context context;
    private FullSpotifyModel fullSpotifyModel;
    private SpotifyApi spotifyApi;
    private CompositeDisposable compositeDisposable;

    public SpotifyOriginPresenter(Context context, BaseViewFragment view, SpotifyApi spotifyApi) {
        this.context = context;
        this.view = view;
        this.spotifyApi = spotifyApi;
        this.compositeDisposable = new CompositeDisposable();
    }

    public void getLatestVersionSpotify() {
        if (UtilsNetwork.isNetworkAvailable(context)) {
            if (QueryPreferneces.isSpotifyBeta(context)) {
                loadDataBeta();
            } else {
                loadDataOrigin();
            }
        } else {
            errorLayout(true);
            view.showNoInternetLayout();
        }
    }

    private void loadDataBeta() {
        compositeDisposable.add(spotifyApi.getLatestOriginBeta()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> {
                    compositeDisposable.add(d);
                    errorLayout(false);
                    view.hideNoInternetLayout();
                    view.showProgress();
                })
                .doOnComplete(() -> {
                    fillData();
                    view.hideProgress();
                    view.showLayoutCards();
                })
                .subscribe(spotify -> {
                    fullSpotifyModel = new FullSpotifyModel(spotify);
                }, throwable -> {
                    errorLayout(true);
                    view.hideProgress();
                    view.showErrorSnackbar(R.string.error);
                }));
    }

    private void loadDataOrigin() {
        compositeDisposable.add(spotifyApi.getLatestOrigin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> {
                    compositeDisposable.add(d);
                    errorLayout(false);
                    view.hideNoInternetLayout();
                    view.showProgress();
                })
                .doOnComplete(() -> {
                    fillData();
                    view.hideProgress();
                    view.showLayoutCards();
                })
                .subscribe(spotify -> {
                    fullSpotifyModel = new FullSpotifyModel(spotify);
                }, throwable -> {
                    errorLayout(true);
                    view.hideProgress();
                    view.showErrorSnackbar(R.string.error);
                }));
    }

    public void downloadLatestVersion() {
        UtilsDownloadSpotify.downloadSpotify(context, fullSpotifyModel.getLatestLink(), fullSpotifyModel.getLatestVersionName());
    }

    private void fillData() {
        view.setLatestVersionAvailable(fullSpotifyModel.getLatestVersionName());
        if (UtilsSpotify.isSpotifyInstalled(context) &&
                UtilsSpotify.isSpotifyUpdateAvailable(UtilsSpotify.getInstalledSpotifyVersion(context), fullSpotifyModel.getLatestVersionNumber())) {
            // Install new version
            view.showFAB();
            view.showCardView();

        } else if (!UtilsSpotify.isSpotifyInstalled(context)) {
            // Install spotify now
            view.showFAB();
            view.showCardView();

        } else if (UtilsSpotify.isDogFoodInstalled(context)) {
            view.showFAB();
            view.showCardView();

        } else {
            //have latest version
            view.hideFAB();
            view.hideCardView();
            view.setInstalledVersion(context.getString(R.string.up_to_date));
        }
    }

    public void checkInstalledSpotifyVersion() {
        if (UtilsNetwork.isNetworkAvailable(context))
            view.showCardView();
        if (UtilsSpotify.isSpotifyInstalled(context)) {
            view.setInstalledVersion(UtilsSpotify.getInstalledSpotifyVersion(context));
            if (!UtilsSpotify.isDogFoodInstalled(context)) {
                view.setUpdateImageFAB();
            }
            getLatestVersionSpotify();
        } else {
            view.showFAB();
            view.setInstallImageFAB();
            view.setInstalledVersion(context.getString(R.string.spotify_not_installed));
            if (hasError) {
                view.hideFAB();
            }
        }
    }

    private void errorLayout(boolean bool) {
        if (bool) {
            hasError = true;
            view.hideLayoutCards();
            view.hideFAB();
        } else {
            hasError = false;
            view.showLayoutCards();
            view.hideFAB();
        }
    }

    public void onDispose() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}

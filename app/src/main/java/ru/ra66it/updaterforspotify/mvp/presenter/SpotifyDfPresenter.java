package ru.ra66it.updaterforspotify.mvp.presenter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;


import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.model.Spotify;
import ru.ra66it.updaterforspotify.mvp.view.BaseViewFragment;
import ru.ra66it.updaterforspotify.rest.SpotifyApi;
import ru.ra66it.updaterforspotify.utils.UtilsDownloadSpotify;
import ru.ra66it.updaterforspotify.utils.UtilsNetwork;
import ru.ra66it.updaterforspotify.utils.UtilsSpotify;


/**
 * Created by 2Rabbit on 10.11.2017.
 */

public class SpotifyDfPresenter {

    private BaseViewFragment viewFragment;
    private String latestLink;
    private String latestVersionName;
    private String latestVersionNumber;
    private String installVersion;
    private boolean hasError = false;
    private Context context;
    private SpotifyApi spotifyApi;

    public SpotifyDfPresenter(Context context, BaseViewFragment viewFragment, SpotifyApi spotifyApi) {
        this.context = context;
        this.viewFragment = viewFragment;
        this.spotifyApi = spotifyApi;
    }


    public void getLatestVersionDf() {
        if (UtilsNetwork.isNetworkAvailable(context)) {
            spotifyApi.getLatestDogFood()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Spotify>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            errorLayout(false);
                            viewFragment.hideNoInternetLayout();
                            viewFragment.showCardProgress();
                            latestVersionNumber = "0.0.0.0";
                        }

                        @Override
                        public void onNext(Spotify spotify) {
                            latestLink = spotify.getBody();
                            latestVersionName = spotify.getName();
                            latestVersionNumber = spotify.getTagName();
                        }

                        @Override
                        public void onError(Throwable e) {
                            errorLayout(true);
                            viewFragment.showErrorSnackbar(R.string.error);
                        }

                        @Override
                        public void onComplete() {
                            checkInstalledSpotifyDfVersion();
                            fillDataDf();
                            viewFragment.hideCardProgress();
                        }
                    });


        } else {
            errorLayout(true);
            viewFragment.showNoInternetLayout();
        }
    }


    public void downloadLatestVersion() {
        UtilsDownloadSpotify.downloadSpotify(context, latestLink, latestVersionName);
    }


    public void fillDataDf() {
        viewFragment.setLatestVersionAvailable(latestVersionName);
        if (UtilsSpotify.isSpotifyInstalled(context) &&
                UtilsSpotify.isDogfoodUpdateAvailable(installVersion, latestVersionNumber)) {
            // Install new version
            viewFragment.showFAB();
            viewFragment.showCardView();

        } else if (!UtilsSpotify.isSpotifyInstalled(context)) {
            // Install spotify now
            viewFragment.showFAB();
            viewFragment.showCardView();

        } else if (!UtilsSpotify.isDogFoodInstalled(context)) {
            viewFragment.showFAB();
            viewFragment.showCardView();

        } else {
            //have latest version
            viewFragment.hideFAB();
            viewFragment.hideCardView();
            viewFragment.setInstalledVersion(context.getString(R.string.up_to_date));
        }


    }

    public void checkInstalledSpotifyDfVersion() {
        if (UtilsNetwork.isNetworkAvailable(context))
            viewFragment.showCardView();
            if (UtilsSpotify.isSpotifyInstalled(context)) {
                installVersion = UtilsSpotify.getInstalledSpotifyVersion(context);
                if (UtilsSpotify.isDogFoodInstalled(context)) {
                    viewFragment.setUpdateImageFAB();
                }
                fillDataDf();
            } else {
                viewFragment.showFAB();
                viewFragment.setInstallImageFAB();
                viewFragment.setInstalledVersion(context.getString(R.string.dogfood_not_installed));
                if (hasError) {
                    viewFragment.hideFAB();
                }
            }
    }

    public void errorLayout(boolean bool) {
        if (bool) {
            hasError = true;
            viewFragment.hideLayoutCards();
            viewFragment.hideFAB();
        } else {
            hasError = false;
            viewFragment.showLayoutCards();
            viewFragment.hideFAB();
        }

    }

}

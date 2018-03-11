package ru.ra66it.updaterforspotify.mvp.view;


/**
 * Created by 2Rabbit on 06.11.2017.
 */

public interface SpotifyView {

    void showProgress();

    void hideProgress();

    void showErrorSnackbar(int stringId);

    void showNoInternetLayout();

    void hideNoInternetLayout();

    void hideCardView();

    void showCardView();

    void hideFAB();

    void setUpdateImageFAB();

    void setInstallImageFAB();

    void showFAB();

    void setInstalledVersion(String installedVersion);

    void showLayoutCards();

    void hideLayoutCards();

    void setLatestVersionAvailable(String latestVersion);
}

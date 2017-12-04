package ru.ra66it.updaterforspotify.mvp.view;




/**
 * Created by 2Rabbit on 06.11.2017.
 */

public interface BaseViewFragment  {

    void showCardProgress();

    void hideCardProgress();

    void showErrorSnackbar(int stringId);

    void showLatestVersion(String version);

    void hideCardView();

    void showCardView();

    void hideFAB();

    void showFAB();

    void setInstalledVersion(String installedVersion);

    void showLayoutCards();

    void hideLayoutCards();

    void setLatestVersionAvailable(String latestVersion);
}

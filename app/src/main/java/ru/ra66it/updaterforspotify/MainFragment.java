package ru.ra66it.updaterforspotify;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.ra66it.updaterforspotify.api.SpotifyDogfoodApi;
import ru.ra66it.updaterforspotify.model.Spotify;
import ru.ra66it.updaterforspotify.utils.UtilsFAB;
import ru.ra66it.updaterforspotify.utils.UtilsNetwork;
import ru.ra66it.updaterforspotify.utils.UtilsSpotify;

import static android.view.View.GONE;

/**
 * Created by 2Rabbit on 28.09.2017.
 */

public class MainFragment extends VisibleFragment {

    private static final String TAG = "MainFragment";

    private static final int REQUEST_CODE = 1;
    private static final String LATEST_VERSION_STATE = "latest_version";
    private static final String LATEST_VERSION_NAME_STATE = "latest_version_name";

    private TextView lblLatestVersion;
    private TextView toolbarSubtitle;
    private TextView lblInstallVersion;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeToRefresh;
    private FloatingActionButton fabDownloadButton;

    private LinearLayout layoutCards;

    private String latestLink;
    private String latestVersionName = "";
    private String latestVersionNumber = "";
    private String installVersion;
    private String fullUrl;

    private boolean hasError = false;


    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        swipeToRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        layoutCards = (LinearLayout) v.findViewById(R.id.layout_cards);
        lblLatestVersion = (TextView) v.findViewById(R.id.lbl_latest_version);
        lblInstallVersion = (TextView) v.findViewById(R.id.lbl_install_version);
        toolbarSubtitle = (TextView) v.findViewById(R.id.toolbar_subtitle);
        progressBar = (ProgressBar) v.findViewById(R.id.latest_progress_bar);
        fabDownloadButton = (FloatingActionButton) v.findViewById(R.id.fab);


        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                swipeToRefresh.setRefreshing(false);
            }
        });

        fabDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeUrl();
                downloadNewVersion(fullUrl);
            }
        });

        fetchData();



        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        checkInstalledSpotifyVersion();
        SpotifyService.setServiceAlarm(getActivity(),
                QueryPreferneces.getNotification(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tool_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void checkInstalledSpotifyVersion() {
        if (UtilsSpotify.isSpotifyInstalled(getActivity())) {
            installVersion = UtilsSpotify.getInstalledSpotifyVersion(getActivity());
            lblInstallVersion.setText(installVersion);
            fabDownloadButton.setImageResource(R.drawable.ic_autorenew_black_24dp);
            fillData();
        } else {
            UtilsFAB.hideOrShowFAB(fabDownloadButton, false);
            lblInstallVersion.setText(getString(R.string.spotify_not_installed));
            fabDownloadButton.setImageResource(R.drawable.ic_file_download_black_24dp);
            if (hasError) {
                UtilsFAB.hideOrShowFAB(fabDownloadButton, true);
            }
        }

    }

    private void fillData() {
        if (!latestVersionNumber.equals("0.0.0.0")) {
            lblLatestVersion.setText(latestVersionNumber);
            if (UtilsSpotify.isSpotifyInstalled(getActivity()) &&
                    UtilsSpotify.isUpdateAvailable(installVersion, latestVersionNumber)) {
                UtilsFAB.hideOrShowFAB(fabDownloadButton, false);
                toolbarSubtitle.setText(getString(R.string.install_new) + latestVersionName);

            } else if (!UtilsSpotify.isSpotifyInstalled(getActivity())) {
                UtilsFAB.hideOrShowFAB(fabDownloadButton, false);
                toolbarSubtitle.setText("Install: " + latestVersionName + " now!");

            } else {
                UtilsFAB.hideOrShowFAB(fabDownloadButton, true);
                toolbarSubtitle.setText(getString(R.string.have_last_version));
            }

            QueryPreferneces.setLatestVersion(getActivity(), latestVersionNumber);
        }
    }

    private void downloadNewVersion(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(getString(R.string.downloading_spotify));
        request.setDescription(getString(R.string.downloading_in));
        request.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                latestVersionName + ".apk");

        DownloadManager manager = (DownloadManager) getActivity()
                .getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void makeUrl() {
        fullUrl = latestLink.split(": ")[1];
    }

    private void fetchData() {

        if (UtilsNetwork.isNetworkAvailable(getActivity())) {
            errorLayout(false);

            latestVersionNumber = "0.0.0.0";
            toolbarSubtitle.setText("");
            toolbarSubtitle.setVisibility(GONE);
            lblLatestVersion.setVisibility(GONE);
            progressBar.setVisibility(View.VISIBLE);


            SpotifyDogfoodApi.Factory.getInstance().getLatest().enqueue(new Callback<Spotify>() {
                @Override
                public void onResponse(Call<Spotify> call, Response<Spotify> response) {

                    try {
                        latestLink = response.body().getBody();
                        latestVersionNumber = response.body().getTagName();
                        latestVersionName = response.body().getName();
                        fillData();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        errorLayout(true);
                        Snackbar.make(swipeToRefresh, getString(R.string.error),
                                Snackbar.LENGTH_SHORT).show();
                    }



                    progressBar.setVisibility(View.GONE);
                    toolbarSubtitle.setVisibility(View.VISIBLE);
                    lblLatestVersion.setVisibility(View.VISIBLE);

                }

                @Override
                public void onFailure(Call<Spotify> call, Throwable t) {
                    errorLayout(true);
                    Snackbar.make(swipeToRefresh, getString(R.string.error),
                            Snackbar.LENGTH_SHORT).show();
                    UtilsFAB.hideOrShowFAB(fabDownloadButton, true);
                }
            });
        } else {
            errorLayout(true);
            toolbarSubtitle.setText(getString(R.string.no_internet_connection));
            Snackbar.make(swipeToRefresh, getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_SHORT).show();
        }



    }

    private void errorLayout(boolean bool) {
        if (bool) {
            hasError = true;
            layoutCards.setVisibility(View.GONE);
            UtilsFAB.hideOrShowFAB(fabDownloadButton, true);
        } else {
            hasError = false;
            layoutCards.setVisibility(View.VISIBLE);
            UtilsFAB.hideOrShowFAB(fabDownloadButton, true);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }
}

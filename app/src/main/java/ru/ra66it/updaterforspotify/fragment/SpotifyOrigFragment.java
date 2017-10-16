package ru.ra66it.updaterforspotify.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.ra66it.updaterforspotify.QueryPreferneces;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.api.SpotifyDogfoodApi;
import ru.ra66it.updaterforspotify.model.Spotify;
import ru.ra66it.updaterforspotify.notification.PollService;
import ru.ra66it.updaterforspotify.notification.VisibleFragment;
import ru.ra66it.updaterforspotify.utils.UtilsDownloadSpotify;
import ru.ra66it.updaterforspotify.utils.UtilsFAB;
import ru.ra66it.updaterforspotify.utils.UtilsNetwork;
import ru.ra66it.updaterforspotify.utils.UtilsSpotify;

import static android.view.View.GONE;

/**
 * Created by 2Rabbit on 08.10.2017.
 */

public class SpotifyOrigFragment extends VisibleFragment {

    private static final String TAG = SpotifyOrigFragment.class.getSimpleName();


    private TextView lblLatestVersion;
    private TextView lblInstallVersion;
    private TextView lblInfo;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeToRefresh;
    private FloatingActionButton fabDownloadButton;

    private LinearLayout layoutCards;

    private String latestLink;
    private String latestVersionName = "";
    private String latestVersionNumber = "";
    private String installVersion;

    private boolean hasError = false;


    public static SpotifyOrigFragment newInstance() {
        return new SpotifyOrigFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spotify_orig_fragment, container, false);


        swipeToRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer_orig);
        layoutCards = (LinearLayout) v.findViewById(R.id.layout_cards_orig);
        lblLatestVersion = (TextView) v.findViewById(R.id.lbl_latest_version_orig);
        lblInstallVersion = (TextView) v.findViewById(R.id.lbl_install_version_orig);
        lblInfo = (TextView) v.findViewById(R.id.lbl_info_orig);
        progressBar = (ProgressBar) v.findViewById(R.id.latest_progress_bar_orig);
        fabDownloadButton = (FloatingActionButton) v.findViewById(R.id.fab_orig);

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
                if (UtilsSpotify.isdDogFoodInstalled(getContext())) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.to_download_spotify_remove)
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                } else {
                    UtilsDownloadSpotify.downloadSpotify(getContext(), latestLink, latestVersionName);
                }
            }
        });

        fetchData();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkInstalledSpotifyVersion();

    }



    private void checkInstalledSpotifyVersion() {
        if (UtilsSpotify.isSpotifyInstalled(getActivity())) {
            installVersion = UtilsSpotify.getInstalledSpotifyVersion(getActivity());
            lblInstallVersion.setText(installVersion);
            if (!UtilsSpotify.isdDogFoodInstalled(getActivity())) {
                fabDownloadButton.setImageResource(R.drawable.ic_autorenew_black_24dp);
            }
            fillData();
        } else {
            UtilsFAB.hideOrShowFAB(fabDownloadButton, false);
            fabDownloadButton.setImageResource(R.drawable.ic_file_download_black_24dp);
            lblInstallVersion.setText(getString(R.string.spotify_not_installed));
            if (hasError) {
                UtilsFAB.hideOrShowFAB(fabDownloadButton, true);
            }
        }

    }

    private void fillData() {
        if (!latestVersionNumber.equals("0.0.0.0")) {
            lblLatestVersion.setText(latestVersionNumber);
            if (UtilsSpotify.isSpotifyInstalled(getActivity()) &&
                    UtilsSpotify.isSpotifyUpdateAvailable(installVersion, latestVersionNumber)) {
                UtilsFAB.hideOrShowFAB(fabDownloadButton, false);
                //Install new version
                lblInfo.setText(getString(R.string.install_new)+ " " + latestVersionName);

            } else if (!UtilsSpotify.isSpotifyInstalled(getActivity())) {
                UtilsFAB.hideOrShowFAB(fabDownloadButton, false);
                //Install spotify now
                lblInfo.setText(getString(R.string.install)+ " " + latestVersionName + " " + getString(R.string.now));

            } else if (UtilsSpotify.isdDogFoodInstalled(getActivity())){
                UtilsFAB.hideOrShowFAB(fabDownloadButton, false);

            } else {
                //have latest version
                UtilsFAB.hideOrShowFAB(fabDownloadButton, true);
                lblInfo.setText(getString(R.string.have_last_version));

            }

        }
    }


    private void fetchData() {
        if (UtilsNetwork.isNetworkAvailable(getActivity())) {
            errorLayout(false);

            latestVersionNumber = "0.0.0.0";
            lblLatestVersion.setVisibility(GONE);
            progressBar.setVisibility(View.VISIBLE);

            SpotifyDogfoodApi.Factory.getInstance().getLatestOrigin().enqueue(new Callback<Spotify>() {
                @Override
                public void onResponse(Call<Spotify> call, Response<Spotify> response) {

                    try {
                        latestLink = response.body().getBody();
                        latestVersionNumber = response.body().getTagName();
                        latestVersionName = response.body().getName();
                        fillData();

                    } catch (NullPointerException e) {
                        errorLayout(true);
                        Snackbar.make(swipeToRefresh, getString(R.string.error),
                                Snackbar.LENGTH_SHORT).show();
                    }


                    progressBar.setVisibility(View.GONE);
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

}

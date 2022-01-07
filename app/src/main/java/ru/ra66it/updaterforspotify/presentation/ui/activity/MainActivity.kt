package ru.ra66it.updaterforspotify.presentation.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.presentation.ui.screen.ErrorScreen
import ru.ra66it.updaterforspotify.presentation.ui.screen.LatestVersionScreen
import ru.ra66it.updaterforspotify.presentation.ui.screen.LoadingScreen
import ru.ra66it.updaterforspotify.presentation.ui.screen.NewVersionScreen
import ru.ra66it.updaterforspotify.presentation.viewmodel.UpdaterViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: UpdaterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdaterApp.applicationComponent.inject(this)

        setContent {
            Screen()
        }
    }

    @Composable
    private fun Screen() {
        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

        val uiState by viewModel.stateFlow.collectAsState()

        when (uiState) {
            is SpotifyStatusState.Error -> {
                val error = uiState as SpotifyStatusState.Error
                ErrorScreen(
                    snackbarHostState = snackbarHostState,
                    errorMessage = error.exception.localizedMessage ?: "",
                    title = getString(R.string.installed_version),
                    subTitle = error.installedVersion,
                    lifecycleOwner = this,
                    cardClickCallback = {
                        viewModel.getLatestSpotify()
                    })
            }
            is SpotifyStatusState.Loading -> {
                val loading = uiState as SpotifyStatusState.Loading
                LoadingScreen(
                    title = getString(R.string.installed_version),
                    subTitle = loading.installedVersion
                )
            }
            is SpotifyStatusState.Data -> {
                val data = (uiState as SpotifyStatusState.Data).spotify
                val permissionRequestLauncher = createPermissionLauncher(data, snackbarHostState)
                val iconId = if (data.spotifyState == spotifyHaveUpdate)
                    R.drawable.ic_autorenew_black_24dp else R.drawable.ic_file_download_black_24dp

                when (data.spotifyState) {
                    spotifyNotInstalled, spotifyHaveUpdate -> NewVersionScreen(
                        snackbarHostState = snackbarHostState,
                        installedTitle = getString(R.string.installed_version),
                        installedVersion = data.installedVersion,
                        latestTitle = getString(R.string.latest_version),
                        latestVersion = data.latestVersionName,
                        buttonTitle = getString(R.string.download),
                        buttonIcon = iconId,
                        clickCallback = {
                            clickDownload(data, permissionRequestLauncher, snackbarHostState)
                        }
                    )
                    spotifyIsLatest -> LatestVersionScreen(
                        title = getString(R.string.installed_version),
                        subTitle = getString(R.string.up_to_date)
                    )
                }
            }
        }
    }

    @Composable
    private fun createPermissionLauncher(
        data: SpotifyData,
        snackbarHostState: MutableState<SnackbarHostState>
    ) =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                downloadFile(data, snackbarHostState)
            } else {
                lifecycleScope.launch {
                    snackbarHostState.value.showSnackbar(getString(R.string.permission_denied))
                }
            }
        }

    private fun clickDownload(
        data: SpotifyData,
        permissionRequestLauncher: ManagedActivityResultLauncher<String, Boolean>,
        snackbarHostState: State<SnackbarHostState>
    ) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            downloadFile(data, snackbarHostState)
        } else {
            permissionRequestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun downloadFile(
        spotifyData: SpotifyData,
        snackbarHostState: State<SnackbarHostState>
    ) {
        val messageId: Int

        if (NetworkChecker.isNetworkAvailable) {
            messageId = R.string.downloading
            viewModel.downloadSpotify(spotifyData)
        } else {
            messageId = R.string.no_internet_connection
        }

        lifecycleScope.launch {
            snackbarHostState.value.showSnackbar(message = getString(messageId))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUI()
    }
}

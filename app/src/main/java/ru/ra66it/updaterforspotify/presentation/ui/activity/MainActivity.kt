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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.ra66it.updaterforspotify.*
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
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
                ErrorScreen(snackbarHostState, error.exception, error.installedVersion)
            }
            is SpotifyStatusState.Loading -> {
                LoadingScreen((uiState as SpotifyStatusState.Loading).installedVersion)
            }
            is SpotifyStatusState.Data -> {
                val data = (uiState as SpotifyStatusState.Data).spotify
                val permissionRequestLauncher = createPermissionLauncher(data, snackbarHostState)
                when (data.spotifyState) {
                    spotifyNotInstalled, spotifyHaveUpdate -> NewVersionScreen(
                        snackbarHostState = snackbarHostState,
                        permissionRequestLauncher = permissionRequestLauncher,
                        data = data
                    )
                    spotifyIsLatest -> LatestVersionScreen()
                }
            }
        }
    }

    @Composable
    private fun LatestVersionScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.colorPrimaryDark)),
        ) {
            InstalledVersionCard(getString(R.string.up_to_date))
        }
    }

    @Composable
    private fun NewVersionScreen(
        snackbarHostState: State<SnackbarHostState>,
        permissionRequestLauncher: ManagedActivityResultLauncher<String, Boolean>,
        data: SpotifyData
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.colorPrimaryDark)),
        ) {
            InstalledVersionCard(data.installedVersion)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp, 0.dp, 32.dp, 32.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = getString(R.string.latest_version),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = data.latestVersionName,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            ExtendedFloatingActionButton(
                text = { Text(text = getString(R.string.download)) },
                icon = {
                    val iconId = if (data.spotifyState == spotifyHaveUpdate)
                        R.drawable.ic_autorenew_black_24dp
                    else
                        R.drawable.ic_file_download_black_24dp

                    Icon(
                        painter = painterResource(id = iconId),
                        ""
                    )
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    downloadSpotify(data, permissionRequestLauncher, snackbarHostState)

                },
                backgroundColor = colorResource(id = R.color.colorAccent)
            )
            Snackbar(snackbarHostState)
        }
    }

    @Composable
    private fun InstalledVersionCard(installedVersion: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = getString(R.string.installed_version),
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = installedVersion,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

            }
        }
    }

    @Composable
    private fun ErrorScreen(
        snackbarHostState: MutableState<SnackbarHostState>,
        error: Exception,
        installedVersion: String
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.colorPrimaryDark)),
        ) {
            InstalledVersionCard(installedVersion)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp, 0.dp, 32.dp, 32.dp)
                    .clickable {
                        viewModel.getLatestSpotify()
                    },
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = R.drawable.ic_autorenew_black_24dp),
                        contentDescription = null,
                    )
                }
            }
            Snackbar(snackbarHostState)
            lifecycleScope.launch {
                snackbarHostState.value.showSnackbar(error.localizedMessage ?: "")
            }
        }

    }

    @Composable
    private fun LoadingScreen(installedVersion: String) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.colorPrimaryDark)),
        ) {
            InstalledVersionCard(installedVersion)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp, 0.dp, 32.dp, 32.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = colorResource(id = R.color.colorPrimaryDark)
                    )
                }
            }
        }
    }

    @Composable
    private fun Snackbar(snackbarHostState: State<SnackbarHostState>) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SnackbarHost(
                modifier = Modifier.align(Alignment.BottomCenter),
                hostState = snackbarHostState.value
            )
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
                    snackbarHostState.value.showSnackbar("PERMISSION DENIED")
                }
            }
        }

    private fun downloadSpotify(
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
        val messageId = if (NetworkChecker.isNetworkAvailable) {
            viewModel.downloadSpotify(spotifyData)
            R.string.downloading
        } else {
            R.string.no_internet_connection
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

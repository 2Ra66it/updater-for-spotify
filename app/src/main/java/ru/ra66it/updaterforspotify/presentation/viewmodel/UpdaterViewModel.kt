package ru.ra66it.updaterforspotify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.domain.usecase.UpdaterUseCase
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import javax.inject.Inject

class UpdaterViewModel @Inject constructor(
    private val interactor: UpdaterUseCase,
    preferences: SharedPreferencesHelper,
    workersEnqueueManager: WorkersEnqueueManager
) : ViewModel() {

    val stateFlow = MutableStateFlow<SpotifyStatusState>(SpotifyStatusState.Loading)

    init {
        workersEnqueueManager.enqueuePeriodicCheckingIfDontExist(
            preferences.isEnableNotification, preferences.checkIntervalDay
        )

        getLatestSpotify()
    }

    fun getLatestSpotify() {
        viewModelScope.launch(Dispatchers.IO) {
            stateFlow.emit(SpotifyStatusState.Loading)

            val state = try {
                val data = interactor.getSpotifyData()
                SpotifyStatusState.Data(data)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()

                SpotifyStatusState.Error(e)
            }

            stateFlow.emit(state)
        }
    }

    fun updateUI() {
        stateFlow.value.also {
            if (it is SpotifyStatusState.Data) {
                val data = interactor.updateInstalledVersion(it.spotify)
                stateFlow.tryEmit(SpotifyStatusState.Data(data))
            }
        }
    }

    fun downloadSpotify() {
        stateFlow.value.also {
            if (it is SpotifyStatusState.Data) {
                val data = it.spotify
                UtilsDownloadSpotify.downloadSpotify(data.latestLink, data.latestVersionNumber)
            }
        }

    }

}
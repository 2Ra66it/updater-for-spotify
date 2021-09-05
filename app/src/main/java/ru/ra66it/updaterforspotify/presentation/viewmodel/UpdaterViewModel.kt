package ru.ra66it.updaterforspotify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.ra66it.updaterforspotify.BuildConfig
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.model.SpotifyData
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.domain.usecase.UpdaterUseCase
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import javax.inject.Inject

class UpdaterViewModel @Inject constructor(
    private val useCase: UpdaterUseCase,
    preferences: SharedPreferencesHelper,
    workersEnqueueManager: WorkersEnqueueManager
) : ViewModel() {

    val stateFlow =
        MutableStateFlow<SpotifyStatusState>(SpotifyStatusState.Loading(useCase.getSpotifyVersion()))

    init {
        workersEnqueueManager.enqueuePeriodicCheckingIfDontExist(
            preferences.isEnableNotification, preferences.checkIntervalDay
        )

        getLatestSpotify()
    }

    fun getLatestSpotify() {
        viewModelScope.launch(Dispatchers.IO) {
            val installedVersion = useCase.getSpotifyVersion()
            stateFlow.emit(SpotifyStatusState.Loading(installedVersion))

            val state = try {
                val data = useCase.getSpotifyData()
                SpotifyStatusState.Data(data)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()

                SpotifyStatusState.Error(e, installedVersion)
            }

            stateFlow.emit(state)
        }
    }

    fun updateUI() {
        stateFlow.value.also {
            if (it is SpotifyStatusState.Data) {
                val data = useCase.updateSpotifyData(it.spotify)
                stateFlow.tryEmit(SpotifyStatusState.Data(data))
            }
        }
    }

    fun downloadSpotify(data: SpotifyData) {
        UtilsDownloadSpotify.downloadSpotify(data.latestLink, data.latestVersionNumber)
    }

}
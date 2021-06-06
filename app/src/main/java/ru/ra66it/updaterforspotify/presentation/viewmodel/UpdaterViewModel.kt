package ru.ra66it.updaterforspotify.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.usecase.UpdaterUseCase
import ru.ra66it.updaterforspotify.domain.model.SpotifyStatusState
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify
import ru.ra66it.updaterforspotify.presentation.workers.WorkersEnqueueManager
import javax.inject.Inject

class UpdaterViewModel @Inject constructor(
    private val interactor: UpdaterUseCase,
    sharedPreferencesHelper: SharedPreferencesHelper,
    workersEnqueueManager: WorkersEnqueueManager
) : ViewModel() {

    val liveData = MutableLiveData<SpotifyStatusState>(SpotifyStatusState.Loading)

    init {
        workersEnqueueManager.enqueuePeriodicCheckingIfDontExist(
            sharedPreferencesHelper.isEnableNotification,
            sharedPreferencesHelper.checkIntervalDay
        )

        getLatestSpotify()
    }

    fun getLatestSpotify() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = interactor.getSpotifyData()
                liveData.postValue(SpotifyStatusState.Data(data))
            } catch (e: Exception) {
                liveData.postValue(SpotifyStatusState.Error(e))
            }
        }
    }

    fun updateUI() {
        liveData.value?.let {
            if (it is SpotifyStatusState.Data) {
                val value = it.spotify
                val data = interactor.updateInstalledVersion(value)
                liveData.value = SpotifyStatusState.Data(data)
            }
        }
    }

    fun downloadSpotify() {
        liveData.value?.let {
            if (it is SpotifyStatusState.Data) {
                val data = it.spotify
                UtilsDownloadSpotify.downloadSpotify(data.latestLink, data.latestVersionNumber)
            }
        }
    }

}
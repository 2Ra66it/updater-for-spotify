package ru.ra66it.updaterforspotify.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.ra66it.updaterforspotify.data.storage.SharedPreferencesHelper
import ru.ra66it.updaterforspotify.domain.interactors.SpotifyInteractor
import ru.ra66it.updaterforspotify.domain.model.Result
import ru.ra66it.updaterforspotify.domain.model.StatusState
import ru.ra66it.updaterforspotify.presentation.service.PollService
import ru.ra66it.updaterforspotify.presentation.utils.SpotifyMapper
import ru.ra66it.updaterforspotify.presentation.utils.UtilsDownloadSpotify
import ru.ra66it.updaterforspotify.R
import kotlin.coroutines.CoroutineContext

class SpotifyViewModel(
        private val spotifyInteractor: SpotifyInteractor,
        private val sharedPreferencesHelper: SharedPreferencesHelper,
        private val spotifyMapper: SpotifyMapper
) : ViewModel(), CoroutineScope {

    private val job = Job()
    val spotifyLiveData: MutableLiveData<StatusState> = MutableLiveData()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun getLatestSpotify() {
        spotifyLiveData.postValue(StatusState.Loading)
        launch {
            val response = withContext(Dispatchers.IO) { spotifyInteractor.getSpotify() }
            when (response) {
                is Result.Success -> {
                    val data = spotifyMapper.map(response.data)
                    spotifyLiveData.postValue(StatusState.Data(data))
                }
                is Result.Error -> {
                    spotifyLiveData.postValue(StatusState.Error(response.exception))
                }
            }
        }
    }

    fun updateUI() {
        if (spotifyLiveData.value != null) {
            if (spotifyLiveData.value is StatusState.Data) {
                val value = (spotifyLiveData.value as StatusState.Data).spotify
                val data = spotifyMapper.updateInstalledVersion(value)
                spotifyLiveData.postValue(StatusState.Data(data))
            }
        }
    }

    fun downloadSpotify() {
        val data = (spotifyLiveData.value as StatusState.Data).spotify
        UtilsDownloadSpotify.downloadSpotify(data.latestLink, data.latestVersionNumber)
    }

    fun startNotification() {
        PollService.setServiceAlarm(sharedPreferencesHelper.isEnableNotification,
                sharedPreferencesHelper.checkIntervalDay)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}
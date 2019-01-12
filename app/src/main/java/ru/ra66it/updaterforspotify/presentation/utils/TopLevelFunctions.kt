package ru.ra66it.updaterforspotify.presentation.utils

import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.data.network.NetworkChecker
import ru.ra66it.updaterforspotify.domain.model.Result
import java.net.ConnectException

suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>): Result<T> = try {
    call.invoke()
} catch (e: Exception) {
    if (NetworkChecker.isNetworkAvailable) {
        Result.Error(e)
    } else {
        Result.Error(ConnectException(StringService.getById(R.string.no_internet_connection)))
    }
}

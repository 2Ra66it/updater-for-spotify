package ru.ra66it.updaterforspotify.presentation.utils

import ru.ra66it.updaterforspotify.domain.Result

suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>): Result<T> = try {
    call.invoke()
} catch (e: Exception) {
    Result.Error(e)
}

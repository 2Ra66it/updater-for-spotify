package ru.ra66it.updaterforspotify.presentation.utils

import ru.ra66it.updaterforspotify.UpdaterApp

object StringService {

    fun getById(id: Int): String {
        return UpdaterApp.instance.resources.getString(id)
    }
}

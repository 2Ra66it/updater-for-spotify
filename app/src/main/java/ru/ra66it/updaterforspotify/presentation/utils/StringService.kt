package ru.ra66it.updaterforspotify.presentation.utils

import ru.ra66it.updaterforspotify.UpdaterApp

/**
 * Created by 2Rabbit on 08.03.2018.
 */

object StringService {

    fun getById(id: Int): String {
        return UpdaterApp.instance.resources.getString(id)
    }
}

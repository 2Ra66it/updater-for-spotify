package ru.ra66it.updaterforspotify.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Created by 2Rabbit on 22.09.2017.
 */

data class Spotify(
        @SerializedName("data") val data: Data
)
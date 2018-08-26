package ru.ra66it.updaterforspotify.domain.models

import com.google.gson.annotations.SerializedName

/**
 * Created by 2Rabbit on 22.09.2017.
 */

data class Spotify (
    @SerializedName("data")  var data: Data
)
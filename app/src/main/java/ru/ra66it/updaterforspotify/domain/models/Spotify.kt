package ru.ra66it.updaterforspotify.domain.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

/**
 * Created by 2Rabbit on 22.09.2017.
 */

class Spotify : Serializable {

    @SerializedName("data")
    @Expose
    var data: Data? = null

}
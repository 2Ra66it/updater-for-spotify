package ru.ra66it.updaterforspotify.domain.models

import com.google.gson.annotations.SerializedName

data class Developer(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String
)
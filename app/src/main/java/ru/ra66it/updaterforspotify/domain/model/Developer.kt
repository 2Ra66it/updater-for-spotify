package ru.ra66it.updaterforspotify.domain.model

import com.google.gson.annotations.SerializedName

data class Developer(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
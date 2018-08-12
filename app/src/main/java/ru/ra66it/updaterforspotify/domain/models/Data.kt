package ru.ra66it.updaterforspotify.domain.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Data : Serializable {

    @SerializedName("id")
    @Expose
    var id: Int = 0
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("package")
    @Expose
    var _package: String? = null
    @SerializedName("uname")
    @Expose
    var uname: String? = null
    @SerializedName("size")
    @Expose
    var size: Int = 0
    @SerializedName("icon")
    @Expose
    var icon: String? = null
    @SerializedName("graphic")
    @Expose
    var graphic: String? = null
    @SerializedName("added")
    @Expose
    var added: String? = null
    @SerializedName("modified")
    @Expose
    var modified: String? = null
    @SerializedName("updated")
    @Expose
    var updated: String? = null
    @SerializedName("main_package")
    @Expose
    var mainPackage: Any? = null
    @SerializedName("developer")
    @Expose
    var developer: Developer? = null
    @SerializedName("file")
    @Expose
    var file: File? = null
}

package ru.ra66it.updaterforspotify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 2Rabbit on 22.09.2017.
 */

public class Spotify implements Serializable{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("version")
    @Expose
    private String version;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }


    public String getVersion() {
        return version;
    }

}

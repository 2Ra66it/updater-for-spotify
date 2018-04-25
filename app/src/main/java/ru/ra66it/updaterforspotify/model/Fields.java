package ru.ra66it.updaterforspotify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Fields implements Serializable
{

    @SerializedName("link")
    @Expose
    private Link link;
    @SerializedName("version")
    @Expose
    private Version version;
    @SerializedName("name")
    @Expose
    private Name name;

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

}

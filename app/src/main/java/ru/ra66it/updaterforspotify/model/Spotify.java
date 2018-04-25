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
    @SerializedName("fields")
    @Expose
    private Fields fields;
    @SerializedName("createTime")
    @Expose
    private String createTime;
    @SerializedName("updateTime")
    @Expose
    private String updateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}

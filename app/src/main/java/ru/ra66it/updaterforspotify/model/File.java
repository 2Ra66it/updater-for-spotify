package ru.ra66it.updaterforspotify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class File {

    @SerializedName("vername")
    @Expose
    private String vername;
    @SerializedName("vercode")
    @Expose
    private int vercode;
    @SerializedName("md5sum")
    @Expose
    private String md5sum;
    @SerializedName("filesize")
    @Expose
    private int filesize;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("path_alt")
    @Expose
    private String pathAlt;

    public String getVername() {
        return vername;
    }

    public void setVername(String vername) {
        this.vername = vername;
    }

    public int getVercode() {
        return vercode;
    }

    public void setVercode(int vercode) {
        this.vercode = vercode;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathAlt() {
        return pathAlt;
    }

    public void setPathAlt(String pathAlt) {
        this.pathAlt = pathAlt;
    }
}
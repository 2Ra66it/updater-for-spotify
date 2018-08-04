package ru.ra66it.updaterforspotify.domain.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("package")
    @Expose
    private String _package;
    @SerializedName("uname")
    @Expose
    private String uname;
    @SerializedName("size")
    @Expose
    private int size;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("graphic")
    @Expose
    private String graphic;
    @SerializedName("added")
    @Expose
    private String added;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("main_package")
    @Expose
    private Object mainPackage;
    @SerializedName("developer")
    @Expose
    private Developer developer;
    @SerializedName("file")
    @Expose
    private File file;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGraphic() {
        return graphic;
    }

    public void setGraphic(String graphic) {
        this.graphic = graphic;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Object getMainPackage() {
        return mainPackage;
    }

    public void setMainPackage(Object mainPackage) {
        this.mainPackage = mainPackage;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

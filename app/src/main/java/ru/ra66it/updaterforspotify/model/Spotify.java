package ru.ra66it.updaterforspotify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 2Rabbit on 22.09.2017.
 */

public class Spotify implements Serializable{

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("assets_url")
    @Expose
    private String assetsUrl;
    @SerializedName("upload_url")
    @Expose
    private String uploadUrl;
    @SerializedName("html_url")
    @Expose
    private String htmlUrl;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("tag_name")
    @Expose
    private String tagName;
    @SerializedName("target_commitish")
    @Expose
    private String targetCommitish;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("draft")
    @Expose
    private boolean draft;
    @SerializedName("author")
    @Expose
    private Author author;
    @SerializedName("prerelease")
    @Expose
    private boolean prerelease;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("published_at")
    @Expose
    private String publishedAt;
    @SerializedName("assets")
    @Expose
    private List<Object> assets = null;
    @SerializedName("tarball_url")
    @Expose
    private String tarballUrl;
    @SerializedName("zipball_url")
    @Expose
    private String zipballUrl;
    @SerializedName("body")
    @Expose
    private String body;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAssetsUrl() {
        return assetsUrl;
    }

    public void setAssetsUrl(String assetsUrl) {
        this.assetsUrl = assetsUrl;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTargetCommitish() {
        return targetCommitish;
    }

    public void setTargetCommitish(String targetCommitish) {
        this.targetCommitish = targetCommitish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.prerelease = prerelease;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<Object> getAssets() {
        return assets;
    }

    public void setAssets(List<Object> assets) {
        this.assets = assets;
    }

    public String getTarballUrl() {
        return tarballUrl;
    }

    public void setTarballUrl(String tarballUrl) {
        this.tarballUrl = tarballUrl;
    }

    public String getZipballUrl() {
        return zipballUrl;
    }

    public void setZipballUrl(String zipballUrl) {
        this.zipballUrl = zipballUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}

package com.example.niephox.methophotos.Entities;

import android.net.Uri;

import com.drew.metadata.Metadata;

import java.net.URI;


/**
 * Created by Niephox on 3/30/2018.
 */

public class Image {
    public String storageLocationURL;
    public String downloadUrl;
    public Uri path;
    public String name;
    public Album album;
    public Metadata metadata;
    public String description;

    public Image(String storageLocationURL, String downloadUrl, String name, Album album, Metadata metadata, String description) {
        this.storageLocationURL = storageLocationURL;
        this.downloadUrl = downloadUrl;
        this.name = name;
        this.album = album;
        this.metadata = metadata;
        this.description = description;
    }
    public Image(Uri path){
        this.path = path;
    }

    public Image (){}

    public Image(String storageLocationURL, String downloadUrl, String description) {
        this.storageLocationURL = storageLocationURL;
        this.downloadUrl = downloadUrl;
        this.description = description;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getStorageLocationURL() {
        return storageLocationURL;
    }

    public void setStorageLocationURL(String storageLocationURL) {
        this.storageLocationURL = storageLocationURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Metadata getMetadata() {
        return metadata;
    }



    public void setMetadata(Metadata metadata) {

        this.metadata = metadata;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

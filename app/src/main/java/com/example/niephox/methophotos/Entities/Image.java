package com.example.niephox.methophotos.Entities;

import com.drew.metadata.Metadata;

/**
 * Created by Niephox on 3/30/2018.
 */

public class Image {
    public String storageURL;
    public String name;
    public Album album;
    public Metadata metadata;
    public String description;

    public Image(String storageURL, String name, Album album, Metadata metadata, String description) {
        this.storageURL = storageURL;
        this.name = name;
        this.album = album;
        this.metadata = metadata;
        this.description = description;
    }

    public String getStorageURL() {
        return storageURL;
    }

    public void setStorageURL(String storageURL) {
        this.storageURL = storageURL;
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

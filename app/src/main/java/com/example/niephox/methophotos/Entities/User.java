package com.example.niephox.methophotos.Entities;

/**
 * Created by Niephox on 3/30/2018.
 */

public class User {
    public String UID;
    public String Username;
    public String Password;
    public String GalleryLink;

    public User(String UID, String username, String password, String galleryLink) {
        this.UID = UID;
        Username = username;
        Password = password;
        GalleryLink = galleryLink;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getGalleryLink() {
        return GalleryLink;
    }

    public void setGalleryLink(String galleryLink) {
        GalleryLink = galleryLink;
    }
}

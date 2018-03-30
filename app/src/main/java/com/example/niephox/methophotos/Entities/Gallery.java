package com.example.niephox.methophotos.Entities;

/**
 * Created by Niephox on 3/30/2018.
 */

public class Gallery {
    public Album album;
    public  String link;

    public Gallery(Album album, String link) {
        this.album = album;
        this.link = link;
    }

    public Album getAlbum() {

        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

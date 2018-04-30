package com.example.niephox.methophotos.Entities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Niephox on 3/30/2018.
 */

public class User {


    private String userUID;
    private String username;
    private ArrayList<Album> albums;


    public User() {
    }

    public User(String userUID, String username, ArrayList<Album> albums) {
        this.userUID = userUID;
        this.username = username;
        this.albums = albums;
    }


    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }


    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void albumsClear()
    {
        this.albums.clear();
    }
    public void addAlbums(ArrayList<Album> albums)
    {
        this.albums.addAll(albums);
    }


}

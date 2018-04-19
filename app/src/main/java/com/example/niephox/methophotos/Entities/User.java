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


    public String userUID;
    public String username;
    public String password;
    public ArrayList<Album> albums;


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public User(){}

    public User(String userUID, String username, String password,ArrayList<Album> albums) {
        this.userUID = userUID;
        this.username = username;
        this.password = password;
        this.albums = albums;
    }

    public void userCreationOnDB(DatabaseReference DBRef , User user) {
       DBRef.child("users").child(user.getUserUID()).setValue(user);
    }
}

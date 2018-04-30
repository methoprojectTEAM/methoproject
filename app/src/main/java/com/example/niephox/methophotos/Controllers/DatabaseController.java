package com.example.niephox.methophotos.Controllers;

import android.provider.MediaStore;
import android.util.Log;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niephox on 4/18/2018.
 */

public class DatabaseController {
    private DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference firebaseUserRef = FirebaseDatabase.getInstance().getReference("/users");
    private FirebaseAuth mAuth ;
    private ArrayList<Image> userImageDataset = new ArrayList<>();
    private GenericTypeIndicator<ArrayList<Image>> genericTypeIndiactor = new GenericTypeIndicator<ArrayList<Image>>(){};
    private ArrayList<Album> userAlbums = new ArrayList<>();
    private  User currentUser=new User(" ","",new ArrayList<Album>());

    public static iAsyncCallback iAsyncCallback;

    public DatabaseController() {

        //getCurrentUser();
    }


    public User returnCurentUser()
    {
        return currentUser;
    }

    public void getCurrentUser() {
        userAlbums.clear();
        currentUser.albums.clear();
        mAuth = FirebaseAuth.getInstance();
        final String currentUserUID = mAuth.getCurrentUser().getUid();
        firebaseUserRef.child(currentUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  String userUID =dataSnapshot.child("userUID").getValue(String.class);
                  String username = dataSnapshot.child("username").getValue(String.class);
//                  currentUser.albums.clear();
                  currentUser.userUID=userUID;
                  currentUser.username=username;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Album album = child.child("albums").getValue(Album.class);
                        userAlbums.add(album);
                    }
                    currentUser.albums.addAll(userAlbums);
                    iAsyncCallback.RetrieveData(2);
                } else {
                    Log.w("User", "User Doesnt exist in Database");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createUser(User user) {
        firebaseUserRef.child(user.getUserUID()).setValue(user);
    }

    public void setUserDisplayNameDatabase() {
        //TODO:: IMPLEMENT
    }

    public void changeUserEmailDatabase(String userUID, String newEmail) {
        firebaseUserRef.child(userUID).child("email").setValue(newEmail);
        //TODO:: IMPLEMENT Call
    }

    public void deleteUserDatabase(String UserUID) {
        firebaseUserRef.child(UserUID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            }
        });
        //TODO:: IMPLEMENT Call
    }

    public void deleteAlbumDatabase() {
        //TODO:: IMPLEMENT
    }

    public void addAlbumDatabase() {
        //TODO:: IMPLEMENT
    }

    public void addAlbumDatabase(User user, Album album) {
        firebaseUserRef.child(user.getUserUID()).child("albums").child(album.name).setValue(album.name);
        firebaseUserRef.child(user.getUserUID()).child("albums").child(album.name).setValue(album);
    }
    public void addImagesToAlbum(User user, String album, Image image) {
        firebaseUserRef.child(user.getUserUID()).child("albums").child(album).child("images").setValue(image);
    }

    public void getUserAlbums() {
        userImageDataset.clear();
        userAlbums.clear();
        currentUser.albums.clear();
        firebaseUserRef.child(currentUser.userUID).child("albums").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {

                                userImageDataset = child.child("images").getValue(genericTypeIndiactor);
                                Date date = child.child("date").getValue(Date.class);
                                String desc = child.child("description").getValue(String.class);
                                String name = child.child("name").getValue(String.class);
                                Album album = new Album(name,date,desc,userImageDataset);
                                userAlbums.add(album);
                            }
                            currentUser.albums.addAll(userAlbums);
                            iAsyncCallback.RefreshView(1);
                            Log.e("size", userAlbums.size()+"");

                        } else {
                            Log.w("DB", "SnapShot Does not exist");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("DBCANCEL", "didnt Get ALbums");
                    }
                });
    }

    public void RegisterCallback(iAsyncCallback iAsyncCallback){this.iAsyncCallback = iAsyncCallback;}

}

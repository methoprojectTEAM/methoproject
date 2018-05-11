package com.example.niephox.methophotos.Controllers;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Niephox on 4/18/2018.
 */

public class DatabaseController {
    private DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference firebaseUserRef = FirebaseDatabase.getInstance().getReference("/users");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<Image> userImageDataset = new ArrayList<>();
    public ArrayList<Album> userAlbums = new ArrayList<>();
    public  User currentUser;
    public static iAsyncCallback iAsyncCallback;

    public DatabaseController() {

        //getCurrentUser();
    }

    public void getCurrentUser() {
        String currentUserUID = mAuth.getCurrentUser().getUid();
        firebaseUserRef.child(currentUserUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //currentUser = dataSnapshot.getValue(User.class);
//                    iAsyncCallback.RetrieveData(2);
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

    public void getUserAlbums(String userUID) {
        userImageDataset.clear();
        userAlbums.clear();
        firebaseUserRef.child(userUID).child("albums").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Album album = child.getValue(Album.class);
                                userAlbums.add(album);
                            }
                            iAsyncCallback.RefreshView(2);

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

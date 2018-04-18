package com.example.niephox.methophotos.Firebase;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.niephox.methophotos.Entities.Image;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Niephox on 4/6/2018.
 */

public class FirebaseAsync extends AsyncTask<String, Integer,ArrayList<Image>> {

    DatabaseReference mDB = FirebaseDatabase.getInstance().getReference();
    ArrayList<Image> ImageSet ;

    @Override
    protected ArrayList<Image> doInBackground(String... strings) {
        mDB.child(String.valueOf(strings)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Image image = dataSnapshot.getValue(Image.class);
                ImageSet.add(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return ImageSet;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Image> images) {
        super.onPostExecute(images);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
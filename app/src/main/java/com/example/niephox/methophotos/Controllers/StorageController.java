package com.example.niephox.methophotos.Controllers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by Niephox on 4/18/2018.
 */

public class StorageController {
    private static String STORAGE_TAG = "FIREBASE STORAGE";
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public static  void DownloadFile (String FileURL){
        StorageReference FileReference = storageReference.child(FileURL);

        File tempFile = null;

        try {
            tempFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileReference.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.w(STORAGE_TAG,"File Successfully downloaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(STORAGE_TAG,"File download failure");
            }
        });

    }
}

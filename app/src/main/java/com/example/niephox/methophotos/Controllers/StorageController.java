package com.example.niephox.methophotos.Controllers;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.example.niephox.methophotos.Activities.MainActivity;
import com.example.niephox.methophotos.Activities.MetadataActivity;
import com.example.niephox.methophotos.Interfaces.RefreshView;
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

public class StorageController   {
    private static String STORAGE_TAG = "FIREBASE STORAGE";
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static File StorageFile;
    public static RefreshView refreshView;

    public static void DownloadFileAndExtractMetadata(String FileURL , final Iterable<JpegSegmentMetadataReader> readers) {
        StorageReference FileReference = storage.getReferenceFromUrl(FileURL);

        File tempFile = null;

        try {
            tempFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File finalTempFile = tempFile;

        FileReference.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                MetadataController.ExtractMetadata(finalTempFile,readers);
                refreshView.UpdateUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public  void registerCallback(RefreshView refreshView){
        this.refreshView = refreshView;
    }

}

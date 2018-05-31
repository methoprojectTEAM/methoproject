package com.example.niephox.methophotos.Controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import  com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE;
import com.example.niephox.methophotos.ViewControllers.PhotosFolderAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Niephox on 4/18/2018.
 */

public class StorageController   {
    private static String STORAGE_TAG = "FIREBASE STORAGE";
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static File StorageFile;
    public  static iAsyncCallback iAsyncCallback;
    public static ArrayList<Image> al_images = new ArrayList<>();

    public static  void DownloadFile(String FileURL , final Iterable<JpegSegmentMetadataReader> readers) {
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
               // MetadataController.ExtractMetadata(finalTempFile,readers);
                StorageFile = finalTempFile;
                iAsyncCallback.RetrieveData( REQUEST_CODE.STORAGE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void UploadPhotos(ArrayList<Image> imagesToUpload){}


    public void registerCallback(iAsyncCallback iAsyncCallback){
        this.iAsyncCallback = iAsyncCallback;
    }


}

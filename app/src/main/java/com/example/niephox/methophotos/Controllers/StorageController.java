package com.example.niephox.methophotos.Controllers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by Niephox on 4/18/2018.
 */

public class StorageController {
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public static  void DownloadFile (String FileURL){
        StorageReference FireReference = storageReference.child(FileURL);

        File tempFile = null;


    }
}

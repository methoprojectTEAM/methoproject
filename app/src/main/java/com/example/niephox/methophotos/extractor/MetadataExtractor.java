package com.example.niephox.methophotos.extractor;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.niephox.methophotos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by Niephox on 3/23/2018.
 */

public   class MetadataExtractor {
    public static void metadataToFile() {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("rainforest.jpg");
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile = localFile;
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        Metadata metadata = null;
                        try {
                            metadata = ImageMetadataReader.readMetadata(finalLocalFile);
                            print(metadata);
                        } catch (ImageProcessingException e) {
                            print(e);
                        } catch (IOException e) {
                            print(e);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });


    }

    private static void print(Metadata metadata ) {
        {
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.print(' ');
            System.out.println();

            //
            // A Metadata object contains multiple Directory objects
            //
            for (Directory directory : metadata.getDirectories()) {

                //
                // Each Directory stores values in Tag objects
                //
                for (Tag tag : directory.getTags()) {
                    System.out.println(tag);
                }

                //
                // Each Directory may also contain error messages
                //
                for (String error : directory.getErrors()) {
                    System.err.println("ERROR: " + error);
                }
            }
        }


    }

    private static void print(Exception exception)
    {
        System.err.println("EXCEPTION: " + exception);
    }
}

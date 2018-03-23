package com.example.niephox.methophotos.extractor;

import android.support.annotation.NonNull;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Niephox on 3/23/2018.
 */


public class MetadataExtractor {

    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static ArrayList<String> metadataList;

    public static ArrayList<String> DownloadFileAndExtractMetadata(String ImageURL, final Iterable<JpegSegmentMetadataReader> readers) {

        StorageReference imageRef = storageRef.child(ImageURL);

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
                        if (readers != null) {
                            ExtractSpecificMetadataType(finalLocalFile, readers);
                        } else {
                            ExtractMetadataFromUnknownFile(finalLocalFile);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //TODO: HANDLE FAILURE
            }
        });
        return metadataList;
    }

    public static void ExtractMetadataFromUnknownFile(File file) {
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
            printMetadata(metadata);
        } catch (ImageProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }
        printMetadata(metadata);
    }

    public static void ExtractSpecificMetadataType(File file, Iterable<JpegSegmentMetadataReader> readers) {
        Metadata metadata = null;
        try {
            // SET READERS Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader(), new IptcReader());
            metadata = JpegMetadataReader.readMetadata(file, readers);
            printMetadata(metadata);
        } catch (JpegProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }
        printMetadata(metadata);
    }


    private static void printMetadata(Metadata metadata) {

        ArrayList<String> extractedMetadata = null;
        for (Directory directory : metadata.getDirectories()) {

            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
                MetadataTag metadataTag = new MetadataTag(tag.getTagType(), directory);
                extractedMetadata.add(metadataTag.toString());
            }
            //
            // Each Directory may also contain error messages
            //
            for (String error : directory.getErrors()) {
                System.err.println("ERROR: " + error);
                extractedMetadata.add(error);
            }
        }
        metadataList = extractedMetadata;
    }


    private static void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }

}

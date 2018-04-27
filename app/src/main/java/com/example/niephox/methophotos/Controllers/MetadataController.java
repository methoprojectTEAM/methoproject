package com.example.niephox.methophotos.Controllers;

import android.util.Log;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.niephox.methophotos.Entities.MetadataTag;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Niephox on 4/20/2018.
 */

public class MetadataController   {
    public static ArrayList<String> metadataList = new ArrayList<>();
    private static  OnSuccessListener<FileDownloadTask.TaskSnapshot> StorageListener;

    public static void ExtractMetadata(File file ,Iterable<JpegSegmentMetadataReader> readers){
        if (readers == null){
            ExtractMetadataFromUnknownFileType(file);
        }
        else{
            ExtractSpecificMetadataType(file,readers);
        }
    }

    private static void ExtractMetadataFromUnknownFileType(File file) {

        Metadata metadata = null;
        metadataList.clear();
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


    private static void ExtractSpecificMetadataType(File file, Iterable<JpegSegmentMetadataReader> readers) {
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

        for (Directory directory : metadata.getDirectories()) {
            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
                MetadataTag metadataTag = new MetadataTag(tag.getTagType(), directory);
                metadataList.add(metadataTag.toString());
            }
            //
            // Each Directory may also contain error messages
            //
            for (String error : directory.getErrors()) {
                System.err.println("ERROR: " + error);
                metadataList.add(error);
            }
        }
        for (int i = 0; i <  MetadataController.metadataList.size(); i++){
            Log.e("METADATA",MetadataController.metadataList.get(i));
        }

    }

    public static void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }

    public ArrayList<String> getMetadataList() {
        return metadataList;
    }



}

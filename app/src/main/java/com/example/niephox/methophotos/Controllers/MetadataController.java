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
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.MetadataTag;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Niephox on 4/20/2018.
 */

public class MetadataController implements iAsyncCallback {
    public static ArrayList<String> metadataList = new ArrayList<>();
    private File File;
    Iterable<JpegSegmentMetadataReader> readers;
    StorageController storageController = new StorageController();
    public static iAsyncCallback iAsyncCallback;

    public void MetadataController() {
        storageController.registerCallback(this);
    }


    public void ExtractMetadata(Image image, Iterable<JpegSegmentMetadataReader> readers) {
       this.readers = readers;
        if (image.getImageURI() == null) {
            String DownloadURL = image.getDownloadUrl();
            StorageController.DownloadFile(DownloadURL, readers);
        } else {
            File = new File(image.getImageURI());
            DataExtractionFromFile();
        }

    }

    private void DataExtractionFromFile() {
        if (readers == null) {
            ExtractMetadataFromUnknownFileType(File);
        } else {
            ExtractSpecificMetadataType(File, readers);
        }
    }


    private void ExtractMetadataFromUnknownFileType(File file) {

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
        //printMetadata(metadata);
    }


    private void ExtractSpecificMetadataType(File file, Iterable<JpegSegmentMetadataReader> readers) {
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

    }


    private void printMetadata(Metadata metadata) {
        metadataList.clear();
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

        iAsyncCallback.RefreshView();
    }

    public void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }


    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
    }

    @Override
    public void RefreshView() {

    }

    @Override
    public void RetrieveData() {
        File = StorageController.StorageFile;
        DataExtractionFromFile();
    }
}

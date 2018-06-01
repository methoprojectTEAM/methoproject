package com.example.niephox.methophotos.Controllers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.tools.FileUtil;
import com.example.niephox.methophotos.Activities.AlbumsViewActivity;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.MetadataTag;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Niephox on 4/20/2018.
 */

public class MetadataController extends AsyncTask<Album, Integer, String> implements iAsyncCallback {
    public static ArrayList<String> metadataList = new ArrayList<>();
    public static ArrayList<String> filteredList = new ArrayList<>();

    private File File;
    Iterable<JpegSegmentMetadataReader> readers = null;
    StorageController storageController = new StorageController();
    public static iAsyncCallback iAsyncCallback;


    private Image image;
    public static Album album;
    private Context context;

    private AlbumsViewActivity act = new AlbumsViewActivity();

    public void setReaders(Iterable<JpegSegmentMetadataReader> readers) {
        this.readers = readers;
    }

    public MetadataController(Image image) {
        this.image = image;
        readers = null;
        storageController.registerCallback(this);
    }

    public MetadataController(Album album ,Context context) {
        this.album = album;
        this.context = context;
        readers = null;
        storageController.registerCallback(this);
    }

    public MetadataController(){}

    public void refreshMetadata(){
        ExtractMetadata(this.image);
    }

    public void ExtractMetadata(Image image) {

            if (image.getImageURI() == null) {
                String DownloadURL = image.getDownloadUrl();
                StorageController.DownloadFile(DownloadURL, readers);
            } else {
                this.File = new File(image.getImageURI());
                DataExtractionFromFile();
            }
//        }else
//        {
//            filteredList.clear();
//            filteredList = image.getMetadata();
//            iAsyncCallback.RetrieveData(REQUEST_CODE.METADATA);
//
    }

    private void DataExtractionFromFile() {
        if (readers == null) {
            ExtractMetadataFromUnknownFileType( );
        } else {
            ExtractSpecificMetadataType( readers);
        }
    }


    private void ExtractMetadataFromUnknownFileType() {

        Metadata metadata = null;
        metadataList.clear();
        try {
            metadata = ImageMetadataReader.readMetadata(this.File);
            printMetadata(metadata);
        } catch (ImageProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }
        //printMetadata(metadata);
    }


    private void ExtractSpecificMetadataType(  Iterable<JpegSegmentMetadataReader> readers) {
        Metadata metadata = null;
        metadataList.clear();
        try {
            // SET READERS Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader(), new IptcReader());
            metadata = JpegMetadataReader.readMetadata(this.File, readers);
            printMetadata(metadata);
        } catch (JpegProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }

    }


    public void printMetadata(Metadata metadata) {
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
        filteredList.clear();
        filteredList.addAll(metadataList);
    }

    public void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }

    public void DownloadedFileComplete() {
        iAsyncCallback.RefreshView(REQUEST_CODE.METADATA);
    }

    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
    }

    @Override
    public void RefreshView(REQUEST_CODE rq) {

    }

    @Override
    public void RetrieveData(REQUEST_CODE rq) {
        switch (rq) {
            case STORAGE:
                this.File = StorageController.StorageFile;
                DataExtractionFromFile();
                DownloadedFileComplete();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context,"We started processing " + album.getName() + " album's images metadata",Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(Album... albums) {
        ArrayList<Image> imagesToProcess= new ArrayList<>();
        ArrayList<Image> proccesedImages = new ArrayList<>();
        imagesToProcess = albums[0].getImages();

        for (int i = 0 ; i< imagesToProcess.size(); i++){
            filteredList.clear();
            ExtractMetadata(imagesToProcess.get(i));
            Image processedImage =  new Image(imagesToProcess.get(i).getImageURI());
            processedImage.setMetadata(filteredList);
            proccesedImages.add(processedImage);
        }
//        for (Image image:imagesToProcess) {
//            filteredList.clear();
//            ExtractMetadata(image);
//            image.setMetadata(filteredList);
//        }
        albums[0].setImages(proccesedImages);
        album = albums[0];
        return  album.getName()+"album's images metadata are now processed";
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
        iAsyncCallback.RetrieveData(REQUEST_CODE.METADATA);
    }
}


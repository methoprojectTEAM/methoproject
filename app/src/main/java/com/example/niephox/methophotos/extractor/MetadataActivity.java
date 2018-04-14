package com.example.niephox.methophotos.extractor;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.niephox.methophotos.Entities.MetadataTag;
import com.example.niephox.methophotos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;

import java.util.ArrayList;


public class MetadataActivity extends AppCompatActivity {

    ImageView image;
    Button btExit;
    TextView textVuew;

    public Button getBtExit() {
       return btExit;
    }

    TextView textView;
    ListView lvMetadata;
    public static ArrayAdapter<String> arrayAdapter;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ArrayList<String> metadataList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metadata);
        lvMetadata = findViewById(R.id.lvMetadata);
        image = findViewById(R.id.imgMetadata);

        Iterable<JpegSegmentMetadataReader> readers = null;

        metadataList.add("will soon be filled");



        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                metadataList);
        lvMetadata.setAdapter(arrayAdapter);
        DownloadFileAndExtractMetadata("rainforest.jpg", readers, this);


    }


    public void DownloadFileAndExtractMetadata(String ImageURL, final Iterable<JpegSegmentMetadataReader> readers, final Activity activity) {

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
                        toast("download success, extracting now..", activity);
                       // if (readers != null) {
                        //    ExtractSpecificMetadataType(finalLocalFile, readers);
                     //   } else {
                            ExtractMetadataFromUnknownFile(finalLocalFile);
                        //}
                        arrayAdapter.notifyDataSetChanged();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                toast("download fail", activity);
            }
        });

    }

    public void ExtractMetadataFromUnknownFile(File file) {
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

    public void ExtractSpecificMetadataType(File file, Iterable<JpegSegmentMetadataReader> readers) {
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


    private void printMetadata(Metadata metadata) {


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

    }


    public void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }

    public static void toast(String message, Activity activity) {
        Context context = activity.getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }


}



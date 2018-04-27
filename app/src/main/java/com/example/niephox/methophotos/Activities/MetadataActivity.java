package com.example.niephox.methophotos.Activities;

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
import com.example.niephox.methophotos.Controllers.MetadataController;
import com.example.niephox.methophotos.Controllers.StorageController;
import com.example.niephox.methophotos.Entities.MetadataTag;
import com.example.niephox.methophotos.Interfaces.RefreshView;
import com.example.niephox.methophotos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;

import java.util.ArrayList;


public class MetadataActivity extends AppCompatActivity implements RefreshView {

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

        metadataList.add("will soon be filled");
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                metadataList);
        lvMetadata.setAdapter(arrayAdapter);

        StorageController controller = new StorageController();
        controller.registerCallback(this);

        Iterable<JpegSegmentMetadataReader> readers = null;
        StorageController.DownloadFileAndExtractMetadata("https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/luca-bravo-500474-unsplash.jpg?alt=media&token=ebbcfb4f-0a95-4dcd-80b4-2b11f0259414", readers);

    }


    @Override
    public void UpdateUI() {
        metadataList.clear();
        metadataList.addAll(MetadataController.metadataList);
        metadataList.add("fasf");
        arrayAdapter.notifyDataSetChanged();
    }
}



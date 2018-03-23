package com.example.niephox.methophotos.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.niephox.methophotos.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PhotoViewActivity extends AppCompatActivity {
    ImageView image;
    ListView lvMetadata;
    public static ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> metadataList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        lvMetadata = findViewById(R.id.listView);



        image = findViewById(R.id.imageView2);
        Uri path=getIntent().getData();

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(path));
            image.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = getContentResolver().openInputStream(path);
            BufferedInputStream bis = new BufferedInputStream(is);
            Metadata metadata = ImageMetadataReader.readMetadata(bis);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    System.out.println(tag);
                }
            }
        } catch (ImageProcessingException e) {
            System.out.println("ERROR");
        } catch (IOException e) {
            System.out.println("error");
        }


    }
}

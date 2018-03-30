package com.example.niephox.methophotos.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.example.niephox.methophotos.Controllers.CustomListViewAdapter;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ImageView[] targetImage = new ImageView[6];
    Uri[] path = new Uri[6];
    int numberPhotos = 0;


//Dimitris Branch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addPhotoButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.buttonDelete);

        Button btmetadata = findViewById(R.id.btMetadata);
        ListView listView = findViewById(R.id.lvImages);

        ArrayList<Image> imageSet = new ArrayList<>();
        imageSet.add(new Image("https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/rainforest.jpg?alt=media&token=abf56b08-099a-473a-b8d9-f6da69b50c30","https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/rainforest.jpg?alt=media&token=abf56b08-099a-473a-b8d9-f6da69b50c30","Description "));
        CustomListViewAdapter adapter = new CustomListViewAdapter(imageSet,getApplicationContext());
        listView.setAdapter(adapter);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream is = getContentResolver().openInputStream(path[0]);
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
        });

        btmetadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getApplicationContext(),MetadataActivity.class);
                startActivity(intent);

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();

            path[numberPhotos] = targetUri;
            Bitmap bitmap;

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage[numberPhotos].setImageBitmap(bitmap);
                setBackgroundColor();
                numberPhotos++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBackgroundColor() {
        targetImage[numberPhotos].setBackgroundColor(Color.WHITE);
    }


    public void toast(String message) {
        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}


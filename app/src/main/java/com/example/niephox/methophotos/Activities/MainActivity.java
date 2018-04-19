package com.example.niephox.methophotos.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.niephox.methophotos.Controllers.DatabaseController;
import com.example.niephox.methophotos.Controllers.RecyclerViewManager;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Firebase.FirebaseAsync;
import com.example.niephox.methophotos.Manifest;
import com.example.niephox.methophotos.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {

    ArrayList<Image> imageSet = new ArrayList<>();
    ArrayList<Image> imageSetLocal = new ArrayList<>();
    ListView localListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DatabaseReference mdbref = FirebaseDatabase.getInstance().getReference("/users");
        //mdbref.child()
        DatabaseController mDBController = new DatabaseController();


        RecyclerView mRecyclerView = findViewById(R.id.OnlineRec);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        imageSet.add(new Image("https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/rainforest.jpg?alt=media&token=abf56b08-099a-473a-b8d9-f6da69b50c30", "https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/rainforest.jpg?alt=media&token=abf56b08-099a-473a-b8d9-f6da69b50c30", "Description "));
        imageSet.add(new Image("https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/rainforest.jpg?alt=media&token=abf56b08-099a-473a-b8d9-f6da69b50c30", "https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/rainforest.jpg?alt=media&token=abf56b08-099a-473a-b8d9-f6da69b50c30", "Description "));


        RecyclerViewManager mAdapter = new RecyclerViewManager(imageSet, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        //Check Permissions for the gallery
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        // Intent intent = new Intent(getApplicationContext(), MetadataActivity.class);
        // startActivity(intent);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(Intent.createChooser(galleryIntent, "select"), 1);

                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        FirebaseAsync task = new FirebaseAsync();
        // task.execute("one");
        MenuInflater Inflaterl = getMenuInflater();
        Inflaterl.inflate(R.menu.image_menu, menu);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while (currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        Image newLocal = new Image(imageUri);
                        imageSetLocal.add(newLocal);
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        currentItem = currentItem + 1;

                    }
                } else if (data.getData() != null) {
                    String imagePath = data.getData().getPath();

                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
                CustomListViewAdapter adapterLocal = new CustomListViewAdapter(imageSetLocal, getApplicationContext());
                localListView.setAdapter(adapterLocal);
            }
        }
    }

    public void toast(String message) {
        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}


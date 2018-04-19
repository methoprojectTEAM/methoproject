package com.example.niephox.methophotos.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
import com.example.niephox.methophotos.Controllers.PhotosFolderAdapter;
import com.example.niephox.methophotos.Controllers.RecyclerViewManager;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Firebase.FirebaseAsync;

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
    public static ArrayList<Image> al_images = new ArrayList<>();
    boolean boolean_folder;
    PhotosFolderAdapter obj_adapter;
    GridView gv_folder;
    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DatabaseReference mdbref = FirebaseDatabase.getInstance().getReference("/users");
        //mdbref.child()
        DatabaseController mDBController = new DatabaseController();

        gv_folder = (GridView)findViewById(R.id.gv_folder);

        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
                intent.putExtra("value",i);
                startActivity(intent);
            }
        });


        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            fn_imagespath();
        }



    }
        /*RecyclerView mRecyclerView = findViewById(R.id.OnlineRec);
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
*/

       //THIS CLASS GETS THE IMAGES FROM DEVICE STORAGE, IT RETURNS A LIST OF IMAGE ENTITY OBJECTS
        public ArrayList<Image> fn_imagespath() {
            al_images.clear();

            int int_position = 0;
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;

            String absolutePathOfImage = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.e("Column", absolutePathOfImage);
                Log.e("Folder", cursor.getString(column_index_folder_name));

                /*for (int i = 0; i < al_images.size(); i++) {
                    if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                        boolean_folder = true;
                        int_position = i;
                        break;
                    } else {
                        boolean_folder = false;
                    }
                }*/


                if (boolean_folder) {

                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.addAll(al_images.get(int_position).getAl_imagepath());
                    al_path.add(absolutePathOfImage);
                    al_images.get(int_position).setAl_imagepath(al_path);

                } else {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfImage);
                    Image obj_model = new Image();
                    //obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                    obj_model.setAl_imagepath(al_path);

                    al_images.add(obj_model);


                }


            }


            for (int i = 0; i < al_images.size(); i++) {
                //Log.e("FOLDER", al_images.get(i).getStr_folder());
                for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
                    Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
                }
            }
            obj_adapter = new PhotosFolderAdapter(getApplicationContext(),al_images);
            gv_folder.setAdapter(obj_adapter);
            return al_images;
        }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            switch (requestCode) {
                case REQUEST_PERMISSIONS: {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            fn_imagespath();
                        } else {
                            Toast.makeText(MainActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(Intent.createChooser(galleryIntent, "select"), 1);
                    //intent calling activities and opening them3
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
                        //ftiaxn
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


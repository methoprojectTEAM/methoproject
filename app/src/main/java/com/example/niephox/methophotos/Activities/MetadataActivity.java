package com.example.niephox.methophotos.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.example.niephox.methophotos.Controllers.MetadataController;
import com.example.niephox.methophotos.Controllers.StorageController;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Arrays;


public class MetadataActivity extends AppCompatActivity implements iAsyncCallback {

    ImageView ivImage;
    ListView lvMetadata;
    public static ArrayAdapter<String> arrayAdapter;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ArrayList<String> metadataList = new ArrayList<String>();
    private MetadataController metadataController = new MetadataController();
    private Image image;
    private String[] listitems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metadata);
        lvMetadata = findViewById(R.id.lvMetadata);
        ivImage = findViewById(R.id.imgMetadata);
        listitems = getResources().getStringArray(R.array.readers_list);


        AlertDialogOnStart();

        metadataList.add("will soon be filled");
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                metadataList);
        lvMetadata.setAdapter(arrayAdapter);
        metadataController.RegisterCallback(this);
    }

    private void ShowImage() {
        if (image.getImageURI() == null) {
            Glide.with(this).load(image.getDownloadUrl()).into(ivImage);
        } else {
            Glide.with(this).load(image.getImageURI()).into(ivImage);
        }
    }

    private void GetImage() {
        int ImageIndex = getIntent().getIntExtra("ImageIndex", 0);
        image = MainActivity.al_images.get(ImageIndex);
    }

    @Override
    public void RefreshView() {
        metadataList.clear();
        metadataList.addAll(MetadataController.metadataList);
        toast(" " + metadataList.size());
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void RetrieveData() {

    }

    private void AlertDialogOnStart() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Select Reader");
        mBuilder.setSingleChoiceItems(listitems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Iterable<JpegSegmentMetadataReader> readers;
                toast(""+which);
                switch (which) {
                    case 0:
                        readers = null;
                        break;
                    case 1:
                        readers = Arrays.asList(new ExifReader(), new IptcReader());
                        break;
                    default:
                        readers = null;
                        break;
                }

                GetImage();
                ShowImage();
                metadataController.ExtractMetadata(image, readers);

                dialog.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    public void toast(String message) {
        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}



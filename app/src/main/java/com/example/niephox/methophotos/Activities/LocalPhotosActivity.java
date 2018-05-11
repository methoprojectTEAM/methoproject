package com.example.niephox.methophotos.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.niephox.methophotos.Controllers.LocalPhotosController;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.Calendar;


public class LocalPhotosActivity extends AppCompatActivity{

    //Controllers:
    private LocalPhotosController localPhotosController; //CREATED BY ALEXANDER


    Context context;
    private final static int REQUEST_PICTURES = 1;

    //Layout Components:
    private Button addButton;

    //Entities:
    private Album localPhotosAlbum;


    private final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_photos_album_layout);

        addButton=(Button) findViewById(R.id.addLocalPhotosButton);
    }


    public void plusButtonFunction(View view)
    {
        localPhotosController = new LocalPhotosController("Local",LocalPhotosActivity.this);
        Activity genActivity = (Activity) context;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if(localPhotosAlbum!=null)
            Toast.makeText(LocalPhotosActivity.this,"hello",Toast.LENGTH_LONG).show();
    }

    public void GetLocalPhotos(Context context) {
        final int REQUEST_PERMISSIONS = 100;
        Activity activity = (Activity) context;
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
        }
    }
    //CREATED BY ALEXANDER FOR IGOR
}

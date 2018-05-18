package com.example.niephox.methophotos.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.niephox.methophotos.Controllers.AlbumRepository;
import com.example.niephox.methophotos.Controllers.AlbumsGridViewAdapter;
import com.example.niephox.methophotos.Controllers.AuthenticationController;
import com.example.niephox.methophotos.Controllers.DatabaseController;
import com.example.niephox.methophotos.Controllers.LocalPhotosController;
import com.example.niephox.methophotos.Controllers.StorageController;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Igor Spiridonov
 */

public class AlbumsViewActivity extends AppCompatActivity implements iAsyncCallback
{
    //ArrayLists:
    public  ArrayList<Album> alAlbums = new ArrayList<>();
    public  ArrayList<Image> alImages = new ArrayList<>();

    //Layout Items:
    private GridView gvAlbums;

    //Controllers:
    private DatabaseController dbController;
    private LocalPhotosController localPhotosController;

    //Adapters:
    private AlbumsGridViewAdapter albumsAdapter;

    //Intents:
    private User curentUser ;
    private Album localAlbum ;
    private AlbumRepository albumController;

    private final int REQUEST_PERMISSIONS = 100;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gvAlbums = (GridView)findViewById(R.id.gv_folder);
        dbController = new DatabaseController();
        dbController.getCurrentUser();

        albumController = new AlbumRepository();

        localAlbum= new Album("Local Photos",null,null,null);

        checkPermissions(AlbumsViewActivity.this);
        AlbumRepository repo = new AlbumRepository();
        localAlbum = repo.getLocalAlbum(this);
        alAlbums.add(localAlbum);

        albumsAdapter = new AlbumsGridViewAdapter(this,alAlbums);
        gvAlbums.setAdapter(albumsAdapter);

        dbController.RegisterCallback(this);
        gvAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AlbumsViewActivity.this,"Album clicked:"+alAlbums.get(position).getName(),Toast.LENGTH_LONG).show();
//                if(position!=0) {
                    alImages.clear();
                    alImages.addAll(alAlbums.get(position).getImages());


                    Intent intent = new Intent(AlbumsViewActivity.this, PhotosViewActivity.class);
                    intent.putExtra("alImages", alImages);
                    startActivity(intent);

//                }
//                else
//                {
//                    Intent intent = new Intent(AlbumsViewActivity.this, LocalPhotosActivity.class);
//                    startActivity(intent);
//                }
            }
        });
    }

    @Override
    public void RefreshView(int RequestCode) {
        switch (RequestCode) {
            case 1:
                alAlbums.addAll(curentUser.getAlbums());
                albumsAdapter.notifyDataSetChanged();
                break;
            case 2:
                break;
        }
    }

    @Override
    public void RetrieveData(int RequestCode) {
        curentUser = dbController.returnCurentUser();
        dbController.getUserAlbums();
        Log.e("alAlbums",alAlbums.size()+"");
        albumsAdapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        localPhotosController.onActivityResult(requestCode, resultCode, data);
    }
    public void testAlbumCreate(View v)
    {
        localPhotosController= new LocalPhotosController("FAMILY",AlbumsViewActivity.this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //fn_imagespath();
                        localAlbum =   albumController.getLocalAlbum(this);
                    } else {
                        Toast.makeText(AlbumsViewActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }
    public void checkPermissions(Context context) {
        final int REQUEST_PERMISSIONS = 100;
        Activity activity = (Activity) context;
        if ((ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
            localAlbum  = albumController.getLocalAlbum(context);
        }
    }
}

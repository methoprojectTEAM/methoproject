package com.example.niephox.methophotos.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.example.niephox.methophotos.Controllers.AlbumsGridViewAdapter;
import com.example.niephox.methophotos.Controllers.AuthenticationController;
import com.example.niephox.methophotos.Controllers.DatabaseController;
import com.example.niephox.methophotos.Controllers.StorageController;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Date;

public class AlbumsViewActivity extends AppCompatActivity implements iAsyncCallback
{
    //ArrayLists:
    public  ArrayList<Image> alImages = new ArrayList<>();
    public  ArrayList<Image> alImages2 = new ArrayList<>();
    public  ArrayList<Album> alAlbums = new ArrayList<>();

    //Layout Items:
    GridView gvAlbums;

    //Controllers:
    DatabaseController dbController;
    AuthenticationController authController;

    //Adapters:
    AlbumsGridViewAdapter albumsAdapter;

    //Strings:
    private String curentUserUid;

    //Intents:
    private User curentUser ;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date = new Date(2018,4,26);

        alImages2.add(new Image("gs://methopro.appspot.com/rainforest.jpg","https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/rainforest.jpg?alt=media&token=abf56b08-099a-473a-b8d9-f6da69b50c30","Just another image"));

        alImages.add(new Image("https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/images%2Fkostas.jpg?alt=media&token=9aba80ad-fdd7-4a63-88b2-cb61d1a8fe70"));


        Album album = new Album("Test Album",date,"Just a test album",alImages);
        Album album1 = new Album("Another Album",date,"Just another album",alImages2);

        gvAlbums = (GridView)findViewById(R.id.gv_folder);
        dbController = new DatabaseController();


        curentUserUid=authController.GetCurrentlySignedUser().getUid();
        curentUser = new User(curentUserUid);

        dbController.addAlbumDatabase(curentUser,album);
        dbController.addAlbumDatabase(curentUser,album1);

        albumsAdapter = new AlbumsGridViewAdapter(this,alAlbums);
        gvAlbums.setAdapter(albumsAdapter);

        dbController.registerCallback(this);
        dbController.getUserAlbums(curentUserUid);
    }

    @Override
    public void UpdateUI()
    {
        alAlbums.clear();
        alAlbums.addAll(dbController.userAlbums);
        albumsAdapter.notifyDataSetChanged();
    }

    @Override
    public void RetrieveData() {

    }
}

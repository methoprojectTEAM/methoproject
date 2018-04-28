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

    public  ArrayList<Album> alAlbums = new ArrayList<>();

    //Layout Items:
    GridView gvAlbums;

    //Controllers:
    DatabaseController dbController;

    //Adapters:
    AlbumsGridViewAdapter albumsAdapter;

    //Strings:


    //Intents:
    private User curentUser ;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gvAlbums = (GridView)findViewById(R.id.gv_folder);
        dbController = new DatabaseController();

        dbController.getCurrentUser();

        albumsAdapter = new AlbumsGridViewAdapter(this,alAlbums);
        gvAlbums.setAdapter(albumsAdapter);

        dbController.RegisterCallback(this);

    }



    @Override
    public void RefreshView(int RequestCode) {
        switch (RequestCode) {
            case 1:
                alAlbums.clear();
                alAlbums.addAll(dbController.userAlbums);
                albumsAdapter.notifyDataSetChanged();
                break;
            case 2:
                break;
        }
    }

    @Override
    public void RetrieveData(int RequestCode) {

        curentUser = dbController.currentUser;
        dbController.getUserAlbums(curentUser.getUserUID());
    }
}

package com.example.niephox.methophotos.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

/**
 * Created by IgorSpiridonov
 */

public class AlbumsViewActivity extends AppCompatActivity implements iAsyncCallback
{
    //ArrayLists:
    public  ArrayList<Album> alAlbums = new ArrayList<>();
    public  ArrayList<Image> alImages = new ArrayList<>();

    //Layout Items:
    GridView gvAlbums;

    //Controllers:
    DatabaseController dbController;

    //Adapters:
    AlbumsGridViewAdapter albumsAdapter;

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
        gvAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AlbumsViewActivity.this,"Album clicked:"+alAlbums.get(position).name,Toast.LENGTH_LONG).show();
                alImages.clear();
                alImages.addAll(alAlbums.get(position).images);

                Intent intent =new Intent(AlbumsViewActivity.this,PhotosViewActivity.class);
                intent.putExtra("alImages",alImages);
                startActivity(intent);
            }
        });
    }

    @Override
    public void RefreshView(int RequestCode) {
        switch (RequestCode) {
            case 1:
                alAlbums.addAll(curentUser.albums);
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
}

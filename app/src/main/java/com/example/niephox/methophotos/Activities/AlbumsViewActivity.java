package com.example.niephox.methophotos.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.example.niephox.methophotos.Controllers.AlbumsControllers.AlbumBuilder;
import com.example.niephox.methophotos.Controllers.AlbumsControllers.AlbumRepository;
import com.example.niephox.methophotos.Controllers.FirebaseControllers.FirebaseService;
import com.example.niephox.methophotos.Controllers.MetadataControllers.MetadataController;
import com.example.niephox.methophotos.Controllers.StorageAdapters.StorageAdapter;
import com.example.niephox.methophotos.ViewControllers.AlbumsAdapter;
import com.example.niephox.methophotos.ViewControllers.ViewAdapters.AlbumsViewActivityView;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AlbumsViewActivity extends AppCompatActivity implements iAsyncCallback, View.OnClickListener {
    //ArrayLists:
    public ArrayList<Album> alAlbums;
    //Layout Items:
    private AlbumsViewActivityView albumsViewActivityView;
    //Controllers:
    private FirebaseService firebaseService;
    private AlbumRepository albumRepo;
    private MetadataController mtcontrol;
    private AlbumBuilder AsalbumBuilder;
    //Intents:
    private User curentUser;
    private Album localAlbum;
    private AlbumsAdapter albumsAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        albumsViewActivityView = new AlbumsViewActivityView(this);

        alAlbums = new ArrayList<>();
        curentUser = new User();
        albumsAdapter = new AlbumsAdapter(this, alAlbums);
        albumsViewActivityView.getRecyclerView().setAdapter(albumsAdapter);
        albumRepo = new AlbumRepository(); //TODO: we create a repo object to manage albums
        localAlbum = albumRepo.generateLocalAlbum(this); //TODO: we generate a local album using the albumRepo
        //AUTOMATIC GENERATION
        mtcontrol = new MetadataController(localAlbum, this);
        mtcontrol.RegisterCallback(this);
        mtcontrol.execute(localAlbum);

        firebaseService = new FirebaseService();
        firebaseService.RegisterCallback(this);
        firebaseService.getCurrentUser();

        alAlbums.add(localAlbum);
    }
    @Override
    public void RefreshView(REQUEST_CODE rq) {
        switch (rq) {
            case STORAGE:
                alAlbums.clear(); //edited code alexander
                alAlbums.add(localAlbum);
                alAlbums.addAll(curentUser.getAlbums());
                albumsAdapter.notifyDataSetChanged();
                break;
            case DATABASE:
                break;
            case AUTOGENERATE:
                alAlbums.addAll(AsalbumBuilder.getAlbumscreated());
                albumsAdapter.notifyDataSetChanged();
                break;
        }
    }
    @Override
    public void RetrieveData(REQUEST_CODE rq) {
        if (rq == REQUEST_CODE.METADATA) {
            localAlbum = mtcontrol.album;
            alAlbums.set(0, localAlbum);
            albumsAdapter.notifyDataSetChanged();
        } else {
            curentUser = new User();
            curentUser = firebaseService.getUser();
            firebaseService.getUserAlbums();
            Log.e("alAlbums", alAlbums.size() + "");
            albumsAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<String> imageURIs = new ArrayList<>();
        if (data != null) { //if user did not select anything
            if (data.getClipData() != null) { //if user selected more than one images, get the images from clipData
                for (int i = 0; i < data.getClipData().getItemCount(); i++)
                    imageURIs.add(StorageAdapter.getRealPathFromURI(this, data.getClipData().getItemAt(i).getUri()));
            } else
                imageURIs.add(StorageAdapter.getRealPathFromURI(this, data.getData())); //if data is not null and theres only one image selected just add the single image uri
        } else //if no Image is selected...
            return;

        //saves the selected images to the album that the repo is managing, and create an album simulteniously
        albumRepo.saveSelectedImages(imageURIs);
        //getting the album that has been created
        alAlbums.add(albumRepo.getAlbum());
        curentUser.addAlbums(alAlbums);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                String[] options = getResources().getStringArray(R.array.AAGoptions);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("Select Option");
                mBuilder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AAGinit("Location");
                                break;
                            case 1:
                                AAGinit("Date");
                                break;
                            default:
                                break;
                        }

                        dialog.dismiss();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void AAGinit(String base){
        switch (base){
            case"Location":
                AsalbumBuilder = new AlbumBuilder(getWindow().getDecorView().findViewById(android.R.id.content), this, AlbumBuilder.AAG_BASE.LOCATION);
                AsalbumBuilder.RegisterCallback(this);
                AsalbumBuilder.execute(localAlbum.getImages());
                break;
            case "Date":
                AsalbumBuilder = new AlbumBuilder(getWindow().getDecorView().findViewById(android.R.id.content), this, AlbumBuilder.AAG_BASE.DATE);
                AsalbumBuilder.RegisterCallback(this);
                AsalbumBuilder.execute(localAlbum.getImages());
                break;
        }

    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addAlbum) {
            albumRepo = new AlbumRepository();
            albumsViewActivityView.getAlbumNameEditText().getText().clear();
            albumsViewActivityView.getAlbumDescriptionEditText().getText().clear();
            albumsViewActivityView.getDialog().show();
            albumsViewActivityView.getCreateAlbumButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!albumsViewActivityView.getAlbumNameEditText().getText().toString().isEmpty() && !albumsViewActivityView.getAlbumDescriptionEditText().getText().toString().isEmpty()) {
                        Album albumToCreate = new Album(albumsViewActivityView.getAlbumNameEditText().getText().toString(), albumsViewActivityView.getAlbumDescriptionEditText().getText().toString());
                        albumRepo.createAlbumFromSelection(albumToCreate, AlbumsViewActivity.this);
                        albumsViewActivityView.getDialog().dismiss();
                    }
                }

            });
        }
    }
}
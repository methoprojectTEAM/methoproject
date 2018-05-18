package com.example.niephox.methophotos.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.niephox.methophotos.Controllers.AlbumsGridViewAdapter;
import com.example.niephox.methophotos.Controllers.DatabaseController;
import com.example.niephox.methophotos.Controllers.StorageController;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements iAsyncCallback {

    GridView gvAlbums;
    AlbumsGridViewAdapter albumsAdapter;
    private final int REQUEST_PERMISSIONS = 100;
    public StorageController storageController = new StorageController();
    public static ArrayList<Image> al_images = new ArrayList<>();
    public static ArrayList<Album> alAlbums = new ArrayList<>();
    DatabaseController dbController;
    private User curentUser;
    boolean boolean_folder;

    Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gvAlbums = (GridView) findViewById(R.id.gv_folder);
        dbController = new DatabaseController();
        dbController.getCurrentUser();
        Button button2 = (Button) findViewById(R.id.button2); //CREATED BY ALEXANDER HAIL RUSSIA
        registerForContextMenu(gvAlbums);

        albumsAdapter = new AlbumsGridViewAdapter(this, alAlbums);
        gvAlbums.setAdapter(albumsAdapter);
        dbController.RegisterCallback(this);

    }
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String permissions[],
        @NonNull int[] grantResults){

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            switch (requestCode) {
                case REQUEST_PERMISSIONS: {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            //fn_imagespath();
                            storageController.fn_imagespath(this);
                        } else {
                            Toast.makeText(MainActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

        }



        @Override
        public boolean onContextItemSelected (MenuItem item){
            // return super.onContextItemSelected(item);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) {
                case R.id.ViewItem:
                    Log.w("MenuItemclicked", "CLICKED  view item");

                    break;
                case R.id.ShowMDItem:
                    Intent ShowMetadataIntent = new Intent(this, MetadataActivity.class);
                    ShowMetadataIntent.putExtra("ImageIndex", info.position);
                    startActivity(ShowMetadataIntent);
                    Log.w("MenuItemclicked", "CLICKED  show metadata ");
                    break;
                case R.id.AddMDItem:
                    //Intent AddMDtent = new Intent(context.getApplicationContext(),PhotoViewActivity.class);
                    //Context.getApplicationContext().startActivity(ViewItemIntent);
                    Log.w("MenuItemclicked", "CLICKED  addItem ");
                    break;
                case R.id.MoveToAlbumItem:
                    //TODO
                    Log.w("MenuItemclicked", "CLICKED  Album");
                    break;
                case R.id.DeleteItem:
                    //TODO
                    Log.w("MenuItemclicked", "CLICKED  Delete");
                    break;
                default:
                    break;
            }
            return true;

        }

        @Override
        public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo
        menuInfo){
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater Inflaterl = getMenuInflater();
            Inflaterl.inflate(R.menu.image_menu, menu);
        }

        @Override
        public void RefreshView ( REQUEST_CODE rq){
            switch (rq) {
                case STORAGE:
                    break;
                case DATABASE:
                    alAlbums.clear();
//                alAlbums.addAll(dbController.userAlbums);
                    albumsAdapter.notifyDataSetChanged();
                    break;
            }
        }

        @Override
        public void RetrieveData ( REQUEST_CODE rq){
            switch (rq) {
                case STORAGE:
                    al_images.clear();
                    al_images.addAll(storageController.al_images);
                    CreateLocalAlbum(al_images);
                    break;
                case DATABASE:
                    curentUser = dbController.returnCurentUser();
                    break;
                default:
                    break;
            }
        }
        private void CreateLocalAlbum (ArrayList < Image > images) {
            Date currentDate = Calendar.getInstance().getTime();
            Album localAlbum = new Album("LocalImages", currentDate, "local Photos", images);
            alAlbums.add(localAlbum);
            albumsAdapter.notifyDataSetChanged();
        }
    }



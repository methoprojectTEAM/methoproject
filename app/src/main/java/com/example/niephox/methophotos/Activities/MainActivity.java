package com.example.niephox.methophotos.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.example.niephox.methophotos.Controllers.AlbumController;
import com.example.niephox.methophotos.Controllers.AlbumsGridViewAdapter;
import com.example.niephox.methophotos.Controllers.DatabaseController;
import com.example.niephox.methophotos.Controllers.PhotosFolderAdapter;
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
    public  ArrayList<Album> alAlbums = new ArrayList<>();
    DatabaseController dbController;
    private User curentUser ;
    boolean boolean_folder;
    //test code starts here
    private ArrayList<Image> selectedImages = null;
    private Image currentImage = new Image();
    private ArrayList<Uri> selectedImageUri = new ArrayList<>();
    private final static int REQUEST_PERMISSION_READ_EXTERNAL = 2;
    private final static int REQUEST_PICTURES = 1; //final
    private final static String TAG_BROWSE_PICTURE = "BROWSE_PICTURE";
    private int currentDisplayedUserSelectImageIndex = 0;
    private AlbumController albumCreateController; //CREATED BY ALEXANDER

    Album album;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gvAlbums = (GridView)findViewById(R.id.gv_folder);
        dbController = new DatabaseController();
        dbController.getCurrentUser();
        Button button2 = (Button) findViewById(R.id.button2); //CREATED BY ALEXANDER HAIL RUSSIA
        registerForContextMenu(gvAlbums);

        albumsAdapter = new AlbumsGridViewAdapter(this,alAlbums);
        gvAlbums.setAdapter(albumsAdapter);
        dbController.RegisterCallback(this);
        GetLocalPhotos(this);
        //storageController.GetLocalPhotos(this);

    }

    /*
    CREATED BY ALEXANDER
     */
    public void testAlbumCreate(View v) {

        albumCreateController = new AlbumController("FAMILY",MainActivity.this);
    }

    //we cannot run onActivityResult to our AlbumController.class because its simply not an activity.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        albumCreateController.onActivityResult(requestCode, resultCode, data);
    }
    /*
    ENDS BY ALEXANDER
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

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
            findImagesPath(context);
        }
    }
    //CREATED BY ALEXANDER FOR IGOR
    public void findImagesPath(Context context) {



        PhotosFolderAdapter obj_adapter;
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);

                Image obj_model = new Image();

                obj_model.setImageURI(absolutePathOfImage);
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
        CreateLocalAlbum(al_images);
       // iAsyncCallback.RetrieveData(1);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
       // return super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.ViewItem:
                Log.w("MenuItemclicked", "CLICKED  view item"  );

                break;
            case R.id.ShowMDItem:
                Intent ShowMetadataIntent = new Intent(this,MetadataActivity.class);
                ShowMetadataIntent.putExtra("ImageIndex",info.position);
                startActivity(ShowMetadataIntent);
                Log.w("MenuItemclicked", "CLICKED  show metadata " );
                break;
            case R.id.AddMDItem:
                //Intent AddMDtent = new Intent(context.getApplicationContext(),PhotoViewActivity.class);
                //Context.getApplicationContext().startActivity(ViewItemIntent);
                Log.w("MenuItemclicked", "CLICKED  addItem ");
                break;
            case R.id.MoveToAlbumItem:
                //TODO
                Log.w("MenuItemclicked", "CLICKED  Album" );
                break;
            case R.id.DeleteItem:
                //TODO
                Log.w("MenuItemclicked", "CLICKED  Delete" );
                break;
            default:
                break;
        }
         return  true;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater Inflaterl = getMenuInflater();
        Inflaterl.inflate(R.menu.image_menu, menu);
    }

    @Override
    public void RefreshView(int RequestCode) {
        switch (RequestCode){
            case 1:
                break;
            case 2:
                alAlbums.clear();
                alAlbums.addAll(dbController.userAlbums);
                albumsAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void RetrieveData(int RequestCode) {
        switch (RequestCode){
            case 1:
                al_images.clear();
                al_images.addAll(storageController.al_images);
                CreateLocalAlbum(al_images);
                break;
            case 2:
                curentUser = dbController.currentUser;
                break;
            default:
                break;
        }
    }
    private void CreateLocalAlbum(ArrayList<Image> images){
        Date currentDate = Calendar.getInstance().getTime();
        Album localAlbum = new Album("LocalImages",currentDate,"local Photos",images);
        alAlbums.add(localAlbum);
        albumsAdapter.notifyDataSetChanged();
    }
}


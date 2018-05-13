package com.example.niephox.methophotos.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.example.niephox.methophotos.Controllers.AlbumRepository;
import com.example.niephox.methophotos.Controllers.AlbumsGridViewAdapter;
import com.example.niephox.methophotos.Controllers.DatabaseController;
import com.example.niephox.methophotos.Controllers.StorageController;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements  iAsyncCallback {

    GridView gvAlbums;
    AlbumsGridViewAdapter albumsAdapter;

    public StorageController storageController = new StorageController();
    public static ArrayList<Image> al_images = new ArrayList<>();
    public  ArrayList<Album> alAlbums = new ArrayList<>();
    DatabaseController dbController;
    private User curentUser ;
    boolean boolean_folder;
    //test code starts here
    User currentUser;
    private final int REQUEST_PERMISSIONS = 100;
    Album album;
    private ArrayList<Image> selectedImages = null;
    private Image currentImage = new Image();
    private ArrayList<Uri> selectedImageUri = new ArrayList<>();
    private final static int REQUEST_PERMISSION_READ_EXTERNAL = 2;
    private final static int REQUEST_PICTURES = 1; //final
    private final static String TAG_BROWSE_PICTURE = "BROWSE_PICTURE";
    private int currentDisplayedUserSelectImageIndex = 0;
    AlbumRepository ctrler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gvAlbums = (GridView)findViewById(R.id.gv_folder);
        dbController = new DatabaseController();
        dbController.getCurrentUser();
        Button button2 = (Button) findViewById(R.id.button2); //CREATED BY ALEXANDER HAIL RUSSIA
        registerForContextMenu(gvAlbums);
        FirebaseStorage storage;
        /*TODO: This chunk is to test uploading an image to
        TODO: firebase storage... delete it when not useful for testing.n
        StorageReference storageReference;
        Uri file = Uri.fromFile(new File("/storage/emulated/0/Download/alextest.jpeg"));
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //alexRef.putFile(file);
        StorageReference ref = storageReference.child("rainforest.jpg");
        ref.putFile(file);
        //StorageReference ref = storageReference.child();
        storageReference.putFile(file);
*/
// Regi
        albumsAdapter = new AlbumsGridViewAdapter(this,alAlbums);
        gvAlbums.setAdapter(albumsAdapter);
       // dbController.RegisterCallback(this);
        //GetLocalPhotos(this);
        //storageController.GetLocalPhotos(this);
        ctrler = new AlbumRepository();
       //album = ctrler.getLocalAlbum(this);
        checkPermissions(this);
       for(Image img : album.getImages())
           Log.w("FINAL IMAGES OF DEVICE", img.getImageURI());
           //ctrler.getLocalAlbum(this);
        ctrler.deleteAlbum(album);
    }


    public void testAlbumCreate(View view) {
        ctrler.createAlbum("Album1", this);
    }
    /*
    CREATED BY ALEXANDER
     */
    //we cannot run onActivityResult to our AlbumRepository.class because its simply not an activity.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       ctrler.onActivityResult(requestCode, resultCode, data);
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
                      album =   ctrler.getLocalAlbum(this);
                    } else {
                        Toast.makeText(MainActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    public void checkPermissions(Context context) {
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
          album  = ctrler.getLocalAlbum(context);
        }
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

    //TODO: Figure out getLocalAlbum functionallity and delete these funcs that are part
    //TODO: of Asyncallback

    @Override
    public void RefreshView(int RequestCode) {
        switch (RequestCode){
            case 1:
                break;
            case 2:
//                alAlbums.clear();
//                alAlbums.addAll(dbController.userAlbums);
//                albumsAdapter.notifyDataSetChanged();
                break;
        }
    }
//TODO: Figure out getLocalAlbum functionallity and delete these funcs that are part
    //TODO: of Asyncallback
    @Override
    public void RetrieveData(int RequestCode) {
        switch (RequestCode){
            case 1:
//                al_images.clear();
//                al_images.addAll(storageController.al_images);
                //CreateLocalAlbum(al_images);
                break;
            case 2:
                curentUser = dbController.currentUser;
                break;
            default:
                break;
        }
    }
//    private void CreateLocalAlbum(ArrayList<Image> images){
//        Date currentDate = Calendar.getInstance().getTime();
//        Album localAlbum = new Album("LocalImages",currentDate,"local Photos",images);
//        alAlbums.add(localAlbum);
//        albumsAdapter.notifyDataSetChanged();
//    }
}


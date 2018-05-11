package com.example.niephox.methophotos.Controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.niephox.methophotos.Activities.MainActivity;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * Created by greycr0w on 4/28/2018.
 */

public class AlbumController {
    private ArrayList<Image> selectedImages;
    private Image currentImage;
    private Album createdAlbum = new Album();
    private final static int REQUEST_PICTURES = 1;
    private Context context; //context is used for selectionImageGallery
    private Activity genActivity; //its important bcs this is controller not an Activity
    private boolean boolean_folder;
    public  static com.example.niephox.methophotos.Interfaces.iAsyncCallback iAsyncCallback;


    public AlbumController() { }

    //to get maiActivity context when calling onActivityResult
    public AlbumController(Context context) {

        this.context = context;
    }
    //you can create an AlbumCreateController by giving the EditText album name, that the user
    //enters and the constructor will throw a pop up screen to the user for selecting pictures saved
    //in device storage.






    public AlbumController(String albumName, Context context) {

        createdAlbum.setName(albumName);
        this.context = context;
        openSelectionImageGallery();

    }


    //Gets all the photos from Device Storage and creates a LocalAlbum
    public Album getLocalAlbum(Context context) {
        ArrayList<Image> tempImageArray = new ArrayList<>();
        int pos = 0;
        Uri uri;
        Cursor cursor;
        int columnIndexData, columnIndexFolderName;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        columnIndexFolderName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(columnIndexData);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(columnIndexFolderName));

            if (boolean_folder) {

                ArrayList<String> imagesPath = new ArrayList<>();
                imagesPath.addAll(tempImageArray.get(pos).getImagesPath());
                imagesPath.add(absolutePathOfImage);
                tempImageArray.get(pos).setImagesPath(imagesPath);

            } else {
                ArrayList<String> imagesPath = new ArrayList<>();
                imagesPath.add(absolutePathOfImage);

                Image tempImage = new Image();

                tempImage.setImageURI(absolutePathOfImage);
                //obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                tempImage.setImagesPath(imagesPath);
                tempImageArray.add(tempImage);

            }


        }

        for (int i = 0; i < tempImageArray.size(); i++) {
            //Log.e("FOLDER", al_images.get(i).getStr_folder());
            for (int j = 0; j < tempImageArray.get(i).getImagesPath().size(); j++) {
                //Log.e("FILE", tempImageArray.get(i).getImagesPath().get(j));
            }
        }

        for(Image img : tempImageArray) {
            Log.w("ALEXANDER ",img.getImageURI());
        }

        Album localAlbum = new Album("Local Album", Calendar.getInstance().getTime(), "This is the autogenerated Album that contains all device Images", tempImageArray);
        return localAlbum;
    }   // iAsyncCallback.RetrieveData(1



    //get created album
    public Album getAlbum () {
        return this.createdAlbum;
    }

    /*
        TODO: create album should be more generic? or should we create a syncOnlineAlbums because we open the selection
        TODO: gallery activity, although we just get the created albums that are saved in the firebase with their storageLocationUrl references
        TODO: that gets the albums all the thumbnails of those albums
     */
        public void createAlbum (String albumName, Context context){

        createdAlbum.setName(albumName);
        this.context = context;
        openSelectionImageGallery();
    }

        private void openSelectionImageGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //allows any image file type. Change * to specific extension to limit it
        intent.setType("image/*");
        //EXTRA_ALLOW_MULTIPLE is an intent property that allows multiple selection of image/* images
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        genActivity = (Activity) context;
        genActivity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICTURES);

    }
    public void onActivityResult ( int requestCode, int resultCode, Intent data) {
        selectedImages = new ArrayList<>();
        currentImage = new Image();
        if (requestCode == REQUEST_PICTURES) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) { //data.getClipData is null
                //count before loop so you dont reset count everiteme
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        currentImage.setImageURI(data.getClipData().getItemAt(i).getUri().toString());
                        currentImage.belongsToAlbumInc();
                        selectedImages.add(currentImage);
                        currentImage = new Image();
                    } //else if there is only one image selected, do this:
                }else if (data.getData() != null) {
                    Uri singleImagePath = data.getData();
                    currentImage.setImageURI(singleImagePath.toString());
                    currentImage.belongsToAlbumInc();
                    selectedImages.add(currentImage);
                }
            }

            if (selectedImages != null) {
                createdAlbum.setImages(selectedImages);
                createdAlbum.setDate(Calendar.getInstance().getTime());
                //getCreatedAlbum(createdAlbum);
            }
     }
     for (Image img : selectedImages)
     Log.w(" TAG ALEXANDER IMAGEs " , img.getImageURI());

    }

    public Album deletePictureFromAlbum (Album sourceAlbum, Image imageToDelete) {
            Album completedDelAlbum = new Album();
        return completedDelAlbum;
    }


    public void deleteAlbum (final Album albumToDelete) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("users").child("xGwcgPWbqeV3QNo3xa0OdxsTGcf2").orderByChild("name").equalTo("album1");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                }
                //                exists = snapshot.exists();
//                iAsyncCallback.RetrieveData(2);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

//    public Album transferImage(Album albumSource, Album albumTarget, ArrayList<Image> selectedImagesToTransfer) {
//        ArrayList<Image> tempImages = new ArrayList<>();
//        tempImages = albumSource.getImages(); //we get all the images of albumSource
//        for (int i = 0; i < selectedImagesToTransfer.size(); i++) {
//            if (!isUriEqual()) { //if the album in which we move the photo to, already exists skip it
//                albumTarget.addImage(selectedImagesToTransfer.get(i)); //kanoume add giati apo thn stigmh pou den exei vrethei to pic sto album to prosthetoume
//                albumSource.getImages().remove(selectedImagesToTransfer.get(i)); //pws epistrefw to allagmeno albumSource????????
//            }else
//                Toast.makeText(context, "image exists in the album you want the image to be transfered to ", Toast.LENGTH_LONG);
//            }
//
//        return albumTarget;
//    }

    public boolean isUriEqual (Image imageSrc, Image imageTar) {
        if (imageSrc.getImageURI().equals(imageTar.getImageURI()))
            return true;
        else
            return false;
    }
    }



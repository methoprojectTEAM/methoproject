package com.example.niephox.methophotos.Controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.example.niephox.methophotos.Activities.MainActivity;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by greycr0w on 4/28/2018.
 */
public class AlbumController {
    private ArrayList<Image> selectedImages = new ArrayList<>();
    private Image currentImage = null;
    private final static int REQUEST_PICTURES = 1;
    private Album createdAlbum = new Album();
    private Context context;
    private Activity genActivity;
    private Date currentDate = null;
    public AlbumController() {

    }
    //to get maiActivity context when calling onActivityResult
    public AlbumController(Context context) {
        this.context = context;
    }
    //you can create an AlbumCreateController by giving the EditText album name, that the user
    //enters and the constructor will throw a pop up screen to the user for selecting pictures saved
    //in device storage.


    //get created albums
    public Album getCreatedAlbum() {
        return createdAlbum;
    }
    //simple all-do constructor
    public AlbumController(String albumName, Context context) {
        createdAlbum.setName(albumName);
        this.context = context;
        openSelectionImageGallery();


    }

    public void createAlbum(String albumName, Context context) {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentImage = new Image();
        if (requestCode == REQUEST_PICTURES) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) { //data.getClipData is null
                    //count before loop so you dont reset count everiteme
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        currentImage.setImageURI(data.getClipData().getItemAt(i).getUri().toString());
                        selectedImages.add(currentImage);
                        currentImage = new Image();
                    } //else if there is only one image selected, do this:
                } else if (data.getData() != null) {
                    Uri singleImagePath = data.getData();
                    currentImage.setImageURI(singleImagePath.toString());
                    selectedImages.add(currentImage);
                }
            }

            if (selectedImages != null) {
                currentDate = Calendar.getInstance().getTime();
                createdAlbum.setImages(selectedImages);
                createdAlbum.setDate(currentDate);
            }
        }

    }

    public Album deletePictureFromAlbum(Album sourceAlbum, Image imageToDelete) {
        Album completedDelAlbum = new Album();
        return completedDelAlbum;
    }
}


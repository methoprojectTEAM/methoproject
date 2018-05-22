package com.example.niephox.methophotos.Controllers;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.niephox.methophotos.Entities.Image;

import java.util.ArrayList;

public class InternalStorageAdapter {
	private ArrayList<Image> imagesArray = new ArrayList<>(); //Style 2 of initialization styles :) idk
	private ArrayList<Image> tempImagesArray = new ArrayList<>();
	private Cursor cursor;
	private int columnIndexData;
	private Activity activity;
	private final String[] projection = { MediaStore.MediaColumns.DATA,
			MediaStore.Images.Media.DISPLAY_NAME };
	private final Uri internalImagesUri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
	private final Uri externalImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;



	public InternalStorageAdapter() {
	}

	public InternalStorageAdapter(Activity activity) {
		this.activity = activity;
	}


	public ArrayList<Image> getAllStorageImages(Activity activity) {
			this.activity = activity;
			loadExternalStorageImages();
			loadInternalStorageImages();
			if(!tempImagesArray.isEmpty())
				imagesArray.addAll(tempImagesArray);
			return imagesArray;
	}


	//I should not be touching the Image entity from here, better return a uri Array????????

	//Loads Internal Storage Images
	private void loadInternalStorageImages() {
		cursor = activity.getContentResolver().query(internalImagesUri, projection, null,
				null, null);
		columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

		while (cursor.moveToNext())
			tempImagesArray.add(new Image(cursor.getString(columnIndexData)));
	}


	//Loads External Storage Images
	private void loadExternalStorageImages() {
		cursor = activity.getContentResolver().query(externalImagesUri, projection, null,
				null, null);
		columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

		while (cursor.moveToNext())
			tempImagesArray.add(new Image(cursor.getString(columnIndexData)));
	}

}



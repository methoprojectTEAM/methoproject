package com.example.niephox.methophotos.Controllers.StorageAdapters;
/*
 * Created by greycr0w on 5/19/2018.
 */
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.niephox.methophotos.Entities.Image;

import java.util.ArrayList;

public class StorageAdapter {
	private ArrayList<Image> imagesArray = new ArrayList<>(); //Style 2 of initialization styles :) idk
	private ArrayList<Image> tempImagesArray = new ArrayList<>();
	private Cursor cursor;
	private int columnIndexData;
	private Activity activity;
	private final String[] projection = { MediaStore.MediaColumns.DATA,
			MediaStore.Images.Media.DISPLAY_NAME };
	private final Uri internalImagesUri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
	private final Uri externalImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;



	public StorageAdapter() { }

	public StorageAdapter(Activity activity) {
		this.activity = activity;
	}


	public ArrayList<Image> getAllStorageImages(Activity activity) {
			this.activity = activity;
			tempImagesArray.clear();
			imagesArray.clear();
			loadInternalStorageImages();
			loadExternalStorageImages();
			imagesArray.addAll(tempImagesArray);
			return imagesArray;
	}



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

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = {MediaStore.Images.Media.DATA};
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}



package com.example.niephox.methophotos.Controllers.FirebaseControllers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v7.app.AppCompatActivity;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.Observable;
import com.example.niephox.methophotos.Interfaces.Observer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StorageService extends AppCompatActivity implements Observable {
	private Album completeAlbum = new Album();
	private User currentUser = new User();
	private ArrayList<Image> uploadedImages = new ArrayList<>();
	private ArrayList<Observer> observers = new ArrayList<>();
	private final StorageReference userStorageReference = FirebaseStorage.getInstance().getReference("/" + currentUser.getUserUID());
	private static FirebaseStorage storage = FirebaseStorage.getInstance();
	private static File StorageFile;

	private int numCores;
	private ThreadPoolExecutor executor;

	public StorageService()  {
		numCores = Runtime.getRuntime().availableProcessors();
		executor = new ThreadPoolExecutor(numCores * 2, numCores *2,
				60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}


	//TODO: THIS IS NOT RUNNING WHEN WE PLAY THE APP, SOMETHING IS GETTING OVERWRITTEN AND THE LISTENERS DIE/
	public void uploadImages(final ArrayList<Image> imagesToUpload, final Album albumDest) {
		uploadedImages = new ArrayList<>();
		completeAlbum = albumDest;
		for(final Image image:imagesToUpload) {
			final Uri fileUri = Uri.fromFile(new File(image.getImageURI()));
			final StorageReference dbRef = userStorageReference.child(albumDest.getName()).child(image.getName()); //reference based on current user to uploadImages in the cloud
			dbRef.putFile(fileUri).addOnSuccessListener(executor, new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					image.setUrls(taskSnapshot.getDownloadUrl(), taskSnapshot.getStorage().toString());
					uploadedImages.add(image);
					if (imagesToUpload.size() == uploadedImages.size()) {
						completeAlbum.setImages(uploadedImages);
						notifyObservers();
					}
				}
			});
		}
	}
	//TODO: IMPLEMENT CALL THROUGH FIREBASE SERVICE, ADD OBSERVER UPDATE, WHEN UPDATED ON FIREBASE JUST CALLBACK
	public void DownloadFile(String FileURL , final Iterable<JpegSegmentMetadataReader> readers) {
		StorageReference FileReference = storage.getReferenceFromUrl(FileURL);

		File tempFile = null;

		try {
			tempFile = File.createTempFile("images", "jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}

		final File finalTempFile = tempFile;

		FileReference.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
				// MetadataController.ExtractMetadata(finalTempFile,readers);
				StorageFile = finalTempFile;
				//iAsyncCallback.RetrieveData( iAsyncCallback.REQUEST_CODE.STORAGE);
				//TODO: IMPLEMENT OBSERBER THROUGH FIREBASE AND WHEN DOENSLOAD IS DONE UPDATE FIREBASE
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {

			}
		});

	}

	public ArrayList<Observer> getObservers() {
		return observers;
	}

	public ArrayList<Image> getUploadedImages() {
		return uploadedImages;
	}

	public Album getCompleteAlbum() {
		return completeAlbum;
	}

	@Override
	public void register(Observer observer) {
			if (!observers.contains(observer))
				observers.add(observer);

	}

	@Override
	public void unregister(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers() {

		for (Observer observer : this.getObservers()) {
			observer.update(this);
		}
//		Intent mService = new Intent();
//		final int job_id = 1000;
//		RSSPullService worker = new RSSPullService( this);
//		worker.onHandleWork(mService);
	}

	private class RSSPullService extends JobIntentService {
		StorageService storageService;
		public RSSPullService(StorageService storageService) {
			this.storageService = storageService;
		}
		@Override
		protected void onHandleWork(@NonNull Intent intent) {
			for (Observer observer : storageService.getObservers()) {
				observer.update(storageService);
			}
		}
	}
}


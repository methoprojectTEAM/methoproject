package com.example.niephox.methophotos.Interfaces;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.widget.Toast;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.security.AccessController.getContext;

public class StorageService implements Subject {
	private Album completeAlbum = new Album();
	private User currentUser = new User();
	private ArrayList<Image> uploadedImages;
	private ArrayList<Observer> observers = new ArrayList<>();
	private final StorageReference userStorageReference = FirebaseStorage.getInstance().getReference("/" + currentUser.getUserUID());

	public StorageService() {

	}
	//TODO: THIS IS NOT RUNNING WHEN WE PLAY THE APP, SOMETHING IS GETTING OVERWRITTEN AND THE LISTENERS DIE/
	public void uploadImages(final ArrayList<Image> imagesToUpload, final Album albumDest) {
		uploadedImages = new ArrayList<>();
		completeAlbum = albumDest;

//		for(final Image image:imagesToUpload) {
//			final Uri fileUri = Uri.fromFile(new File(image.getImageURI()));
//			final StorageReference dbRef = userStorageReference.child(image.getName()); //reference based on current user to uploadImages in the cloud
//
//					dbRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//						@Override
//						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//							image.setUrls(taskSnapshot.getDownloadUrl(), taskSnapshot.getStorage().toString());
//							uploadedImages.add(image);
//						}
//					});
//					if(imagesToUpload.equals(uploadedImages)) {
//						completeAlbum.setImages(uploadedImages);
//						notifyObservers();
//					}
//		}
	}

//	private Image uploadImage(Image imageToUploadAsync) {
//		HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
//		handlerThread.start();
//		Looper looper = handlerThread.getLooper();
//		Handler handler = new Handler(looper);
//		handler.post(new Runnable(){
//			@Override
//			public void run() {}
//		});
//	}
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
		//for(final Observer observer:observers) {
//			LongOperation longOperation = new LongOperation(this);
//
//			int mCorePoolSize = 60;
//			int mMaximumPoolSize = 80;
//			int mKeepAliveTime = 10;
//			LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(mMaximumPoolSize);
//			Executor mCustomThreadPoolExecutor = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, TimeUnit.SECONDS, workQueue);
//			longOperation.executeOnExecutor(mCustomThreadPoolExecutor);
		Intent mService = new Intent();
		final int job_id = 1000;
		RSSPullService worker = new RSSPullService( this);
		worker.onHandleWork(mService);


		//	observer.update(this);
		//}
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
//	private class LongOperation extends AsyncTask<Void, Void , Void> {
//		private StorageService storageService = new StorageService();
//
//		public LongOperation(StorageService storageService) {
//			this.storageService = storageService;
//		}
//
//		@Override
//		protected Void doInBackground(Void... voids) {
//			for (Observer observer : storageService.getObservers()) {
//				observer.update(storageService);
//			}
//			return null;


}


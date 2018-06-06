package com.example.niephox.methophotos.Tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.Observer;
import com.example.niephox.methophotos.Interfaces.Observable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

//TODO: THIS TASK HAS TO BE RUN ASYNCHRONOUSLY BECAUSE ITS STOPPING THE MAIN THREAD FROM RUNNING UI REFRESH
public class UploadImageTask extends AsyncTask<ArrayList<Uri>, Void, ArrayList<Uri>> implements Observable {
	private static final String TAG = "UploadDocsAsyncTask";
	User currentUser = new User();
	private final StorageReference userStorageReference = FirebaseStorage.getInstance().getReference("/" + currentUser.getUserUID());
	ArrayList<Image> uploadedImages = new ArrayList<>();
	ArrayList<Image> imagesToPutRefs = new ArrayList<>();
	ArrayList<Observer> observers = new ArrayList<>();
	Album finishedAlbum = new Album();
	ArrayList<Uri> uris;
	public Album getFinishedAlbum() {
		return finishedAlbum;
	}

	public ArrayList<Observer> getObservers() {
		return observers;
	}

	public ArrayList<Image> getUploadedImages() {
		return uploadedImages;
	}

	public UploadImageTask(ArrayList<Image> imagesToUpload, Album doneAlbum) {
		this.finishedAlbum = doneAlbum;
		this.imagesToPutRefs = imagesToUpload;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}


	protected ArrayList<Uri> doInBackground(ArrayList<Uri>... documents) {
		final ArrayList<UploadTask> tasks = new ArrayList<>();
		final ArrayList<Uri> downloadUrls = new ArrayList<>();
		final ArrayList<String> storageLocationUrls = new ArrayList<>();

		for (final Image image : finishedAlbum.getImages()) {
			Uri fileUri = Uri.fromFile(new File(image.getImageURI()));
			final StorageReference dbRef = userStorageReference.child(image.getName()); //reference based on current user to uploadImages in the cloud
			//UploadTask uploadTask = dbRef.putFile(fileUri);
			dbRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					image.setUrls(taskSnapshot.getDownloadUrl(), taskSnapshot.getStorage().toString());
					uploadedImages.add(image);
					downloadUrls.add(taskSnapshot.getDownloadUrl());
//					if (uploadedImages.size() == finishedAlbum.getImages().size()) {
//						finishedAlbum.setImages(uploadedImages);
//					}
				}
			});

			Log.d(TAG, "All upload tasks created");

			try {
				Log.d(TAG, "Waiting...");
				Tasks.await(Tasks.whenAll(tasks));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
			Log.d(TAG, "End of background processing");
			return downloadUrls;

		}


		@Override
		protected void onPostExecute (ArrayList < Uri > downloadUrls) {
			super.onPostExecute(downloadUrls);
			Log.d(TAG, "Post-Execute: Size=" + downloadUrls.size());
			if(uploadedImages!= null)
			finishedAlbum.setImages(uploadedImages);
			notifyObservers();
			// TODO: do something with the downloadUrls
			//mProgView.setVisibility(View.GONE);

		}

		@Override
		public void register (Observer observer){
			if (!observers.contains(observer))
				observers.add(observer);
		}

		@Override
		public void unregister (Observer observer){
			observers.remove(observer);
		}

		@Override
		public void notifyObservers () {
			UploadImageTask accessToTask = this;
			for (Observer observer : accessToTask.getObservers()) {
				observer.update(accessToTask);
			}
		}

}

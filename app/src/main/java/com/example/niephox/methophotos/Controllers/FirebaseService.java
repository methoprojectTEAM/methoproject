package com.example.niephox.methophotos.Controllers;

/*
 * Created by greycr0w on 5/1/2018.
 */

import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.Observer;
import com.example.niephox.methophotos.Interfaces.StorageService;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.Tasks.UploadImageTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseService implements Observer{

	private User currentUser = new User();
	//TODO: final FIREBASE STRUCTURAL REFERENCES
	private final DatabaseReference firebaseUserAlbumsRef = FirebaseDatabase.getInstance().getReference("/users/" + currentUser.getUserUID() + "/albums");
	private final DatabaseReference firebaseUserRef = FirebaseDatabase.getInstance().getReference("/users/");
	private final StorageReference userStorageReference = FirebaseStorage.getInstance().getReference("/" + currentUser.getUserUID());
	StorageService storageService = new StorageService();
	UploadImageTask uploadImageTask;
	private ArrayList<Album> userAlbums = new ArrayList<>();
	private GenericTypeIndicator<Map<String, Album>> albumsGenericTypeIndicator = new GenericTypeIndicator<Map<String, Album>>() {};
 	private static com.example.niephox.methophotos.Interfaces.iAsyncCallback iAsyncCallback;


	//TODO: CHANGE THE WAY ANY CHANGES TO THE DATABASE ARE MADE, THERE IS NOT NEED TO DOWNLOAD ALL DATA WHEN SOMETHING SMALL LIKE AN EMAIL IS CHANGED
	public FirebaseService() {
	}

	public void createUser(User user) {
		firebaseUserRef.child(user.getUserUID()).setValue(user);
	}



	public User getUser()
	{
		return currentUser;
	}

	public void getUserAlbums() {
		firebaseUserAlbumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Map<String, Album> map = dataSnapshot.getValue(albumsGenericTypeIndicator);
				if(map != null)
					userAlbums.addAll(map.values());
				iAsyncCallback.RefreshView(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.STORAGE);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("Database CANCEL", "Didnt Get Albums");
			}
		});
	}

	public void getCurrentUser() {
		firebaseUserRef.child(currentUser.getUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Map<String, Album> albumMap = dataSnapshot.child("albums").getValue(albumsGenericTypeIndicator);
				if(albumMap != null)
					userAlbums.addAll(albumMap.values());
				currentUser.addAlbums(userAlbums);
				currentUser.setUserUID(dataSnapshot.child("userUID").getValue(String.class));
				currentUser.setUsername(dataSnapshot.child("username").getValue(String.class));
				iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.DATABASE);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});


	}


	public void queryChangeUserEmail(String newEmail) {
		firebaseUserRef.child("email").setValue(newEmail);
		//TODO:: IMPLEMENT Call
	}
	//IF THESE DONT WORK CHECK THE firebaseUserRef final reference up above for possible corrections
	public void queryDeleteUser() {
		firebaseUserRef.child(currentUser.getUserUID()).getRef().removeValue();
//		firebaseUserRef.removeValue(new DatabaseReference.CompletionListener() {
//			@Override
//			public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//			}
//		});
//		//TODO:: IMPLEMENT Call
	}
//
	//Asynchronous call-back method
	public void RegisterCallback(iAsyncCallback iAsyncCallback) {
		this.iAsyncCallback = iAsyncCallback;
	}

	//Deletes album selected
	public void queryAlbumDelete(String albumToDelete) {
		firebaseUserAlbumsRef.child(albumToDelete).getRef().removeValue();
	}

	public Image setImage(Image imageToAddRefs, UploadTask.TaskSnapshot snap) {

		return imageToAddRefs;
	}

	//TODO: add a help function to check if the image the user wants to transfer exists in the cloud
	//TODO: finish workaround indexing and finish whole implementation of fire-base service
	public void addImageToAlbum(final Image imageToAdd, final Album albumDest) {
		//albumDest.addImage(imageToAdd);
		firebaseUserAlbumsRef.child(albumDest.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				final Album albumToAddImageTo = dataSnapshot.getValue(Album.class);
				final StorageReference dbRef = userStorageReference.child(imageToAdd.getName()); //reference based on current user to uploadImages in the cloud
				Uri fileUri = Uri.fromFile(new File(imageToAdd.getImageURI()));
				dbRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
						imageToAdd.setUrls(taskSnapshot.getDownloadUrl(), taskSnapshot.getStorage().toString());
						albumToAddImageTo.addImage(imageToAdd);
						firebaseUserAlbumsRef.child(albumDest.getName()).setValue(albumToAddImageTo); //Stores the album in database with the correct references
						}

				});
				//ArrayList<Image> images = new ArrayList<>();
				//HashMap<String, Object> map;

//					Album album1 = new Album();
//					album1.setDate(dataSnapshot.child("date").getValue(Date.class));
//					album1.setDescription(dataSnapshot.child("description").getValue(String.class));
				//FOUND DA WAY ALRIGHET
//				albumToAddImageTo.addImage(imageToAdd);
//				firebaseUserAlbumsRef.child(albumDest.getName()).setValue(albumToAddImageTo);
//				HashMap<String, Album> album = new HashMap<>();
//				album.put(dataSnapshot.getKey(),dataSnapshot.getValue(Album.class));
//					HashMap<String, Image> images = new HashMap<>();
//					Album newAlbum = album.get(albumDest.getName());
//					for(DataSnapshot snap : dataSnapshot.child("images").getChildren()) {
//						images.put(snap.getKey(), snap.getValue(Image.class));
//					}


//				if(map!=null)
//				Log.w("IMAGE", map.get("sadad") );
				HashMap<String, Image> imageMap = new HashMap<>();
//				for(DataSnapshot image: dataSnapshot.getChildren()) {
//					imageMap.put(image.getKey(), image.getValue());
//				}
//				if(map != null)
//				for(Object actualImages:images) {
//
//				}
//					images.addAll(map.values());
//				images.add(imageToAdd);
//				albumDest.setImages(images);
//				firebaseUserAlbumsRef.child(albumDest.getName()).setValue(albumDest);




//
//					albumDest.getImages().addAll(map.values());

				//albumDest.addImage(imageToAdd);
				//images.addAll(albumDest.getImages());
				//firebaseUserAlbumsImagesRef.setValue(images);

				//iAsyncCallback.RefreshView(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.STORAGE);

			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("Database CANCEL", "Didnt Get Albums");
			}
		});

//		imagesGenericTypeIndicator
		//imageHashMap.;
		//firebaseUserAlbumsRef.child("album2").child("images").updateChildren()
	}
	//TODO: TRANSFERRING AN IMAGE FROM ONE ALBUM TO ANOTHER WILL CAUSE DUPLICATION ISSUES AND THE BUG SCALES
	public void deleteImageFromAlbum(final Image imageToDelete, String albumSource) {
		//firebaseUserAlbumsRef.child(albumToDelete).getRef().removeValue();

		Query qr = firebaseUserAlbumsRef.child(albumSource).child("images").orderByChild("name").equalTo(imageToDelete.getName()).getRef();
		qr.getRef().removeValue();
		//queryAlbumDelete("albumone");

	}
//	public Image uploadImage(final Image imageToUpload) {
//		Uri fileUri = Uri.fromFile(new File(imageToUpload.getImageURI()));
//		final StorageReference dbRef = userStorageReference.child(imageToUpload.getName()); //reference based on current user to uploadImages in the cloud
//		final UploadTask uploadTask = dbRef.putFile(fileUri);
//		final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//			@Override
//			public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//				if (!task.isSuccessful()) {
//					throw task.getException();
//				}
//				imageToUpload.setStorageLocationURL(dbRef.getStorage().toString());
//				imageToUpload.setDownloadUrl(dbRef.getDownloadUrl().toString());
//				// Continue with the task to get the download URL
//				return dbRef.getDownloadUrl();
//
//			}
//		}).addOnCompleteListener(new OnCompleteListener<Uri>() {
//			@Override
//			public void onComplete(@NonNull Task<Uri> task) {
//				if (task.isSuccessful()) {
//					Uri downloadUri = task.getResult();
//				} else {
//					// Handle failures
//					// ...
//				}
//			}
//		});
//		return imageToUpload;
//	}
	//TODO: you should not uploadImages images here you should user a different function to uploadImages images and then uploadImages the album because
	//TODO: the function to just uploadImages images is needed in case we want to transfer photos from local album to another album
	//TODO: Implement a view imageUploadUpdate with the updated album (album that has the downloadUrl and StorageLocUrl references)
	//getting all the image uris->uploading the images to the firebase storage->creating the album with the correct storage and dowload urls images
	public void queryAlbumCreate(final Album albumToUpload) {
//		final ArrayList<Image> tempUploadedImages = albumToUpload.getImages(); //temp image array to store FINAL IMAGES WITH REFERENCES
//		for(final Image image:albumToUpload.getImages()) {
//			Uri fileUri = Uri.fromFile(new File(image.getImageURI()));
//			final StorageReference dbRef = userStorageReference.child(image.getName()); //reference based on current user to uploadImages in the cloud
//			//UploadTask uploadTask = dbRef.putFile(fileUri);
//			dbRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//				@Override
//				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//					image.setUrls(taskSnapshot.getDownloadUrl(), taskSnapshot.getStorage().toString());
//					if (tempUploadedImages.size() == albumToUpload.getImages().size()) {
//						albumToUpload.setImages(tempUploadedImages);
		//storageService.register(this);
//		this.uploadImageTask = new UploadImageTask(albumToUpload.getImages(), albumToUpload);
//		uploadImageTask.register(this);
//		ArrayList<Uri> imageUris = new ArrayList<>();
//		for(Image images:albumToUpload.getImages()) {
//			imageUris.add(Uri.parse(images.getImageURI()));
//		}
		//uploadImageTask
		//uploadImageTask.execute(imageUris);
//		final StorageService storageService = new StorageService(); //class variable
		StorageService storageService = new StorageService();
		storageService.register(this);
//		HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
//		handlerThread.start();
//		Looper looper = handlerThread.getLooper();
//		Handler handler = new Handler(looper);
//		handler.post(new Runnable() {
//						 @Override
//						 public void run() {
							//this happens async
							 storageService.uploadImages(albumToUpload.getImages(), albumToUpload);
//						 }
//
//					 });

						//firebaseUserAlbumsRef.child(albumToUpload.getName()).setValue(albumToUpload); //Stores the album in database with the correct references

//					}
//				}
//			});
//
//		}
	}



	//TODO: later deduplication of images, check each image you are about to uploadImages if it already exists in the cloud
	public boolean imageExists(Image image) {

		return false;
	}
	//what if i implement this observer here to have 2 different methods attached to it and then create another update that
	//ti allo mporei na kanei observe o firebase service?
	//TODO: AFOU DIMIOURGOUME DIAFORETIKA STORAGESERVICE OBJECTS GIA UPLOAD KAI DOWNLOAD TOTE TO ENA APO TA DUO GET THA EINAI NULL.
	@Override
	public void update(Object objectToCastTo) {
		storageService = (StorageService) objectToCastTo;

			Album albumToUpload = storageService.getCompleteAlbum();
			firebaseUserAlbumsRef.child(albumToUpload.getName()).setValue(albumToUpload);

	}
}

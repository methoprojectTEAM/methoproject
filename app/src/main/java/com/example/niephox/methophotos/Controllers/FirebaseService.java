package com.example.niephox.methophotos.Controllers;

/*
 * Created by greycr0w on 5/1/2018.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.Observer;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class FirebaseService implements Observer{

	private  User currentUser = new User();
	private  ArrayList<Album> userAlbums = new ArrayList<>();

	//TODO: final FIREBASE STRUCTURAL REFERENCES
	private  final DatabaseReference firebaseUserAlbumsRef = FirebaseDatabase.getInstance().getReference("/users/" + currentUser.getUserUID() + "/albums");
	private  final DatabaseReference firebaseUserRef = FirebaseDatabase.getInstance().getReference("/users/");
	private  final StorageReference userStorageReference = FirebaseStorage.getInstance().getReference("/" + currentUser.getUserUID());


	StorageService storageService = new StorageService();
	private  GenericTypeIndicator<Map<String, Album>> albumsGenericTypeIndicator = new GenericTypeIndicator<Map<String, Album>>() {};
 	private  com.example.niephox.methophotos.Interfaces.iAsyncCallback iAsyncCallback;
	private Context context;

	//TODO: CHANGE THE WAY ANY CHANGES TO THE DATABASE ARE MADE, THERE IS NOT NEED TO DOWNLOAD ALL DATA WHEN SOMETHING SMALL LIKE AN EMAIL IS CHANGED

	public FirebaseService() {

	}
	public FirebaseService(Context context) {
		this.context = context;
	}

	//TODO: AUTHENTICATION CONTROLLER IS CALLING THIS FUNCTION TO CREATE AN ENTRY?
	//TODO: IS THIS USED FOR UPDATING THE USER DATA IN DATABASE
	public void createUser(User user) {
		firebaseUserRef.child(user.getUserUID()).setValue(user);
	}


	public User getUser() {
		return currentUser;
	}

	public void getUserAlbums() {
		firebaseUserAlbumsRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				userAlbums.clear();
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
	//TODO: THIS IS CALLED ONCE WHEN THE USER FIRST LOGINS. ANY CHANGES TO THE USER SHOULD NOT BE LISTENED TO BECAUSE IT WILL GET UNESSECARY DATA LIKE ALBUMS
	public  void getCurrentUser() {
		firebaseUserRef.child(currentUser.getUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Map<String, Album> albumMap = dataSnapshot.child("albums").getValue(albumsGenericTypeIndicator);
				currentUser.albumsClear();
				if(albumMap != null)
					userAlbums.addAll(albumMap.values());
				currentUser.setAlbums(userAlbums);
				currentUser.setUserUID(dataSnapshot.child("userUID").getValue(String.class));
				currentUser.setUsername(dataSnapshot.child("username").getValue(String.class));
				iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.DATABASE);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});


	}

	//TODO: OBSERVE tHE getCurrentUser METHOD AND THE SINGLEVALUEEVENT LISTENER WHEN THE EMAIL OF THE USER IS CHANGED. OBSERVER IF ITS RUNNING TWICE.
	public void queryChangeUserEmail(String newEmail) {
		firebaseUserRef.child("email").setValue(newEmail);
		//TODO:: IMPLEMENT Call
	}


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

	//TODO: DELETION OF FOLDER IN STORAGE IS NOT YET SUPPORTED, LOOP THROUGH ALL THE ALBUM FOLDER IMAGES AND DELETE ONE BY ONE
	public  void queryAlbumDelete(final String albumToDelete) {

		firebaseUserAlbumsRef.child(albumToDelete).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				//TODO:USE STORAGE SERVICE TO DELETE ALL PHOTOS
				String ref = FirebaseStorage.getInstance().getReference().toString();//TODO: THE STORAGELOCATIONURL IS GOING TO BE USED!!!!!!!!!!!!!! FOR DELETING THE IMAGES SO WE NEED TO GET AN ALBUM OBJECT
				Log.w("REF", ref);
				/*.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Log.w("SUCCESS DELETING", "DELETED");
					}
				});*/
				if(dataSnapshot!=null) {
					dataSnapshot.getRef().removeValue();
				}
//				else
					//TODO: HANDLE ALBUM DOESNT EXIST
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	public Image setImage(Image imageToAddRefs, UploadTask.TaskSnapshot snap) {
		return imageToAddRefs;
	}



	//im uploading again in case the user is transfering from the local album
	public void addImageToAlbum(final Image imageToAdd, final String albumDest) {

		firebaseUserAlbumsRef.child(albumDest).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				final Album albumToAddImageTo = dataSnapshot.getValue(Album.class);
				if (!dataSnapshot.exists()) {
					Toast.makeText(context, "This album does not exist online", Toast.LENGTH_LONG).show();
//					return;
				} else {
					final StorageReference dbRef = userStorageReference.child(imageToAdd.getName()); //reference based on current user to uploadImages in the cloud
					Uri fileUri = Uri.fromFile(new File(imageToAdd.getImageURI()));
					dbRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
							imageToAdd.setUrls(taskSnapshot.getDownloadUrl(), taskSnapshot.getStorage().toString());
							albumToAddImageTo.addImage(imageToAdd);
							firebaseUserAlbumsRef.child(albumDest).setValue(albumToAddImageTo); //Stores the album in database with the correct references
						}

					});
				}
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("Database CANCEL", "Didnt Move Image");
			}
		});
	}

	public void deleteImageFromAlbum(final Image imageToDelete, final String albumSource) {
		firebaseUserAlbumsRef.child(albumSource).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				final Album albumToDeleteImage = dataSnapshot.getValue(Album.class);
				if (!dataSnapshot.exists()) {
					Toast.makeText(context, "This album does not exist online", Toast.LENGTH_LONG).show();
				} else {
					ArrayList<Image> tempImages = albumToDeleteImage.getImages();
					for (int i = 0; i < tempImages.size(); i++) {
						if (tempImages.get(i).getName().equals(imageToDelete.getName())) {
							tempImages.remove(i);
						}
					}
					albumToDeleteImage.setImages(tempImages);
					firebaseUserAlbumsRef.child(albumToDeleteImage.getName()).setValue(albumToDeleteImage);
					//iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.DATABASE);
				}
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

	}


	public void queryAlbumCreate(final Album albumToUpload) {
		StorageService storageService = new StorageService();
		storageService.register(this);
		storageService.uploadImages(albumToUpload.getImages(), albumToUpload);

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

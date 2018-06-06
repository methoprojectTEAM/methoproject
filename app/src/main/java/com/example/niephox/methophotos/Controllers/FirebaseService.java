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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class FirebaseService implements Observer{

	private static User currentUser = new User();
	//TODO: final FIREBASE STRUCTURAL REFERENCES
	private static final DatabaseReference firebaseUserAlbumsRef = FirebaseDatabase.getInstance().getReference("/users/" + currentUser.getUserUID() + "/albums");
	private static final DatabaseReference firebaseUserRef = FirebaseDatabase.getInstance().getReference("/users/");
	private static final StorageReference userStorageReference = FirebaseStorage.getInstance().getReference("/" + currentUser.getUserUID());


	StorageService storageService = new StorageService();
	private static ArrayList<Album> userAlbums = new ArrayList<>();
	private static GenericTypeIndicator<Map<String, Album>> albumsGenericTypeIndicator = new GenericTypeIndicator<Map<String, Album>>() {};
 	private static com.example.niephox.methophotos.Interfaces.iAsyncCallback iAsyncCallback;
	private Context context;

	//TODO: CHANGE THE WAY ANY CHANGES TO THE DATABASE ARE MADE, THERE IS NOT NEED TO DOWNLOAD ALL DATA WHEN SOMETHING SMALL LIKE AN EMAIL IS CHANGED

	public FirebaseService() {

	}
	public FirebaseService(Context context) {
		this.context = context;
	}


	public static void createUser(User user) {
		firebaseUserRef.child(user.getUserUID()).setValue(user);
	}


	public static User getUser() {
		return currentUser;
	}

	public static void getUserAlbums() {
		userAlbums.clear();
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

	public static void getCurrentUser() {
		userAlbums.clear();
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
	public static void queryAlbumDelete(String albumToDelete) {
		firebaseUserAlbumsRef.child(albumToDelete).getRef().removeValue();
		iAsyncCallback.RefreshView(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.DATABASE);
	}

	public Image setImage(Image imageToAddRefs, UploadTask.TaskSnapshot snap) {
		return imageToAddRefs;
	}


	//im uploading again in case the user is transfering from the local album
	public static void addImageToAlbum(final Image imageToAdd, final String albumDest) {

		firebaseUserAlbumsRef.child(albumDest).addListenerForSingleValueEvent(new ValueEventListener() {
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
						firebaseUserAlbumsRef.child(albumDest).setValue(albumToAddImageTo); //Stores the album in database with the correct references
						}

				});
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
				if(albumToDeleteImage==null) {
					Toast.makeText(context, "This image does not exist online", Toast.LENGTH_LONG).show();
					return;
				}
				ArrayList<Image> tempImages = albumToDeleteImage.getImages();
				for(int i = 0; i < tempImages.size(); i++) {
					if (tempImages.get(i).getImageURI().equals(imageToDelete.getImageURI())) {
						tempImages.remove(i);
					}
				}
				albumToDeleteImage.setImages(tempImages);
				firebaseUserAlbumsRef.child(albumToDeleteImage.getName()).setValue(albumToDeleteImage);
				iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.DATABASE);
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

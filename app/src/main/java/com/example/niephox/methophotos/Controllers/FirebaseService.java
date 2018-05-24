package com.example.niephox.methophotos.Controllers;

/*
 * Created by greycr0w on 5/1/2018.
 */

import android.net.Uri;
import android.util.Log;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class FirebaseService {

	private User currentUser = new User();
	//TODO: final FIREBASE STRUCTURAL REFERENCES
	private final DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
	private final DatabaseReference firebaseUserAlbumsRef = FirebaseDatabase.getInstance().getReference("/users/" + currentUser.getUserUID() + "/albums");
	private DatabaseReference firebaseUserRef = FirebaseDatabase.getInstance().getReference("/users/" + currentUser.getUserUID());
	private final StorageReference userStorageReference = FirebaseStorage.getInstance().getReference("/" + currentUser.getUserUID());
	private Query query;
	private Album createdAlbumInCloud = new Album();
	private ArrayList<Album> userAlbums = new ArrayList<>();
	private GenericTypeIndicator<Map<String, Album>> albumsGenericTypeIndicator = new GenericTypeIndicator<Map<String, Album>>() {};
	private static com.example.niephox.methophotos.Interfaces.iAsyncCallback iAsyncCallback;



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
		//userAlbums.clear();
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
		//currentUser.albumsClear();
		firebaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				Map<String, Album> map = dataSnapshot.child("albums").getValue(albumsGenericTypeIndicator);
				if(map != null)
					userAlbums.addAll(map.values());
				currentUser.addAlbums(userAlbums);
				currentUser.setUserUID(dataSnapshot.child("userUID").getValue(String.class));
				currentUser.setUsername(dataSnapshot.child("username").getValue(String.class));
				iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.DATABASE);

				//Log.w("User", "User Doesnt exist in Database");

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
		firebaseUserRef.removeValue(new DatabaseReference.CompletionListener() {
			@Override
			public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
			}
		});
		//TODO:: IMPLEMENT Call
	}

	public void RegisterCallback(iAsyncCallback iAsyncCallback) {
		this.iAsyncCallback = iAsyncCallback;
	}


	public void queryAlbumDelete(Album albumToDelete) {
		query = firebaseUserAlbumsRef.orderByChild("name").equalTo(albumToDelete.getName());
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot dummySnapshot:snapshot.getChildren()) {
					dummySnapshot.getRef().removeValue();
				}
				//lastSnap.getRef().removeValue();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}

		});
	}



	public void queryTransferImage(final Image image, Album fromAlbum, final Album toAlbum) {
		/*//delete the image with the current uri from album and add it to the other
		query = ref.child("users").child(user.getUserUID()).child("albums").child("name").child(fromAlbum.getName()).child("images").orderByChild("ImageURI").equalTo(image.getImageURI());
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot stock : snapshot.getChildren()) {
					if (stock.getValue().equals(image.getImageURI()))
						stock.getRef().removeValue();
				}
				ref.child("users").child(user.getUserUID()).child("albums").child(toAlbum.getName()).child("images").setValue(image);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {

			}

		});
		*/
	}
	//getting all the image uris->uploading the images to the firebase storage->creating the album with the correct storage and dowload urls images
	public void queryAlbumCreate(final Album albumToUpload) {
		final ArrayList<Image> tempUploadedImages = albumToUpload.getImages(); //temp image array to store FINAL IMAGES WITH REFERENCES
		createdAlbumInCloud = albumToUpload;
		for(final Image image:albumToUpload.getImages()) {
			Uri myUri = Uri.parse(image.getImageURI());
			StorageReference dbRef = userStorageReference.child(image.getName()); //reference based on current user to upload in the cloud
			dbRef.putFile(myUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					image.setDownloadUrl(taskSnapshot.getDownloadUrl().toString());
					image.setStorageLocationURL(taskSnapshot.getStorage().toString());
					//if the
					if (tempUploadedImages.size() == albumToUpload.getImages().size()) {
						createdAlbumInCloud.setImages(tempUploadedImages);
						//Stores the album in the database with the correct references
						if(createdAlbumInCloud.getName()!=null) {
							firebaseUserAlbumsRef.child(createdAlbumInCloud.getName()).setValue(createdAlbumInCloud);
							iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.DATABASE);
						}
					}

				}
			});
		}
	}



	//TODO: later deduplication of images, check each image you are about to upload if it already exists in the cloud
	void imageExists(Image image) {

	}
}

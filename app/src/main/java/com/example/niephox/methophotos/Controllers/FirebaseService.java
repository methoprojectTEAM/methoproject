package com.example.niephox.methophotos.Controllers;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseService {
	DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
	Query query;
	User user = new User();
	public FirebaseService() {
	}

	public void queryAlbumDelete(Album albumToDelete) {
		query = ref.child("users").child(user.getUserUID()).child("albums").orderByChild("name").equalTo(albumToDelete.getName());
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				DataSnapshot lastSnap;
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

	public void queryAlbumCreate(Album albumToCreate) {
		ref.child("users").child(user.getUserUID()).child("albums").child(albumToCreate.getName()).setValue(albumToCreate);
	}

	public void queryTransferImage(final Image image, Album fromAlbum, final Album toAlbum) {
		//delete the image with the current uri from album and add it to the other
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
	}
}

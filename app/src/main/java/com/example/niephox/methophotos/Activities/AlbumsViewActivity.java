package com.example.niephox.methophotos.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.example.niephox.methophotos.Controllers.AlbumBuilder;
import com.example.niephox.methophotos.Controllers.AlbumRepository;
import com.example.niephox.methophotos.Controllers.FirebaseService;
import com.example.niephox.methophotos.Controllers.MetadataController;
import com.example.niephox.methophotos.ViewControllers.AlbumsAdapter;
import com.example.niephox.methophotos.ViewControllers.GridSpacingItemDecoration;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import com.example.niephox.methophotos.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor Spiridonov
 */

public class AlbumsViewActivity extends AppCompatActivity implements iAsyncCallback, View.OnClickListener {
	//ArrayLists:
	public ArrayList<Album> alAlbums = new ArrayList<>();

	//Controllers:
	FirebaseService firebaseService;
	private AlbumRepository albumRepo;
	private AlbumBuilder albumBuilder;

	//Intents:
	private User curentUser;
	private Album localAlbum;
	private RecyclerView recyclerView;
	private AlbumsAdapter adapter;

	private Album album2;

	private final int REQUEST_PERMISSIONS = 100;


	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		alAlbums.clear();

		setView();
		firebaseService = new FirebaseService();
		firebaseService.getCurrentUser();
		albumRepo = new AlbumRepository();
		localAlbum = new Album();
		album2 = new Album("Album2", "Desc");
		checkPermissions(AlbumsViewActivity.this);
		alAlbums.add(localAlbum);
		firebaseService.RegisterCallback(this);


	}

	public void setView() {
		initCollapsingToolbar();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
		actionBar.setDisplayHomeAsUpEnabled(true);
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		adapter = new AlbumsAdapter(this, alAlbums);
		RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(adapter);
		FloatingActionButton floatingActionButton =
				(FloatingActionButton) findViewById(R.id.addAlbum);
		floatingActionButton.setOnClickListener(this);
	}

	@Override
	public void RefreshView(REQUEST_CODE rq) {
		switch (rq) {
			case STORAGE:
				alAlbums.clear(); //edited code alexander
				alAlbums.add(localAlbum);
				alAlbums.addAll(curentUser.getAlbums());
				adapter.notifyDataSetChanged();
				break;
			case DATABASE:
				break;
		}
	}

	@Override
	public void RetrieveData(REQUEST_CODE rq) {
		if (rq == REQUEST_CODE.METADATA) {
			alAlbums.addAll(albumBuilder.getAlbumsGenerated());
			adapter.notifyDataSetChanged();
		} else {
			curentUser = firebaseService.getUser();
			firebaseService.getUserAlbums();
			Log.e("alAlbums", alAlbums.size() + "");
			adapter.notifyDataSetChanged();
		}

	}

	public InputStream GetInputStream( Uri uri) {
		ContentResolver cr = getApplicationContext().getContentResolver();

		try {
			InputStream istr = cr.openInputStream(uri);
			cr.getType(uri);
			return istr;
		} catch (Exception e) {
			Log.w("File not found", e);
		}
	return null;
	}

    //WRITTEN BY PETALIDIS:::::::::::::::::::::::::::::::::::::::
    @Override
	@TargetApi(24)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String path;
		ArrayList<InputStream> inputStreamsOfSelectedImages = new ArrayList<>();
        List<Uri> imageURIs = new ArrayList<>();
        if (data != null) { //if user did not select anything
            if (data.getClipData() != null) { //if user selected more than one images, get the images from clipData
                for (int i = 0; i < data.getClipData().getItemCount(); i++)
                    imageURIs.add(data.getClipData().getItemAt(i).getUri());

            } else {
				imageURIs.add(data.getData()); //if data is not null and theres only one image selected just add the single image uri

//				InputStream in = new InputStream() {
//					@Override
//					public int read() throws IOException {
//						return 0;
//					}
//				};
//				try {
//					in = getContentResolver().openInputStream(data.getData());
//					ExifInterface exifInterface = new ExifInterface(in);
//					String LATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//					String LATITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
//					String LONGITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
//					String LONGITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
//				} catch (IOException e) {
//					// Handle any errors
//				} finally {
//					if (in != null) {
//						try {
//							in.close();
//						} catch (IOException ignored) {}
//					}
//				}
					//Log.w("REAL PATH:",path);

				/*Uri uri = data.getData();
				Uri docUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
				String path = getPath(,docUri);*/
            }
        } else
            return;

        for (Uri uri:imageURIs) {
			try {
				inputStreamsOfSelectedImages.add(getContentResolver().openInputStream(uri));
			} catch (Exception e) {
				Log.w("File not found", e);
			}
		}
		MetadataController ctr = new MetadataController();
		Metadata metadata = null;
		try {
			metadata = ImageMetadataReader.readMetadata(inputStreamsOfSelectedImages.get(0));
			Log.w("METADATA" , metadata.toString());
			ctr.printMetadata(metadata);
		} catch (ImageProcessingException e) {
			Log.w("THAT EXC" , e);
		} catch (IOException e) {
			Log.w("THAT EXC" , e);

		}
		//printMetadata
        //ctrl.ExtractMetadata();
       /* ArrayList<String> newPaths = new ArrayList<>();
        for(Uri uri:imageURIs) {
        	newPaths.add(getPath(uri.toString()));

		}*/
        //saves the selected images to the album that the repo is managing
        albumRepo.saveSelectedImages(imageURIs);
        //getting the album that has been created
        alAlbums.add(albumRepo.getAlbum());
        albumRepo.createAlbum(albumRepo.getAlbum());
        curentUser.addAlbums(alAlbums);
        adapter.notifyDataSetChanged();
    }

	public void dumpImageMetaData(Uri uri) {

		// The query, since it only applies to a single document, will only return
		// one row. There's no need to filter, sort, or select fields, since we want
		// all fields for one document.
		Cursor cursor = this.getContentResolver()
				.query(uri, null, null, null, null, null);

		try {
			// moveToFirst() returns false if the cursor has 0 rows.  Very handy for
			// "if there's anything to look at, look at it" conditionals.
			if (cursor != null && cursor.moveToFirst()) {

				// Note it's called "Display Name".  This is
				// provider-specific, and might not necessarily be the file name.
				String displayName = cursor.getString(
						cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				Log.i("TAG", "Display Name: " + displayName);

				int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
				// If the size is unknown, the value stored is null.  But since an
				// int can't be null in Java, the behavior is implementation-specific,
				// which is just a fancy term for "unpredictable".  So as
				// a rule, check if it's null before assigning to an int.  This will
				// happen often:  The storage API allows for remote files, whose
				// size might not be locally known.
				String size = null;
				if (!cursor.isNull(sizeIndex)) {
					// Technically the column stores an int, but cursor.getString()
					// will do the conversion automatically.
					size = cursor.getString(sizeIndex);
				} else {
					size = "Unknown";
				}
				Log.i("TAG", "Size: " + size);
			}
		} finally {
			cursor.close();
		}
	}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        localAlbum = albumRepo.generateLocalAlbum(this);
                    } else {
                        Toast.makeText(AlbumsViewActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                albumBuilder = new AlbumBuilder();
                albumBuilder.RegisterCallback(this);
                albumBuilder.buildBasedOnDate(localAlbum.getImages());
                //xTODO:: add drawer
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkPermissions(Context context) {
        final int REQUEST_PERMISSIONS = 100;
        Activity activity = (Activity) context;
        if ((ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
            localAlbum = albumRepo.generateLocalAlbum(context);
           //AUTOMATIC GENERATION



        }
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);


        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addAlbum) {
            final AlertDialog.Builder diaBuilder = new AlertDialog.Builder(AlbumsViewActivity.this);
            albumRepo = new AlbumRepository();
            View createAlbumView = getLayoutInflater().inflate(R.layout.layout_create_album, null);
            final EditText albumName = (EditText) createAlbumView.findViewById(R.id.albumName);
            final EditText albumDescription = (EditText) createAlbumView.findViewById(R.id.albumDescription);
            Button createAlbum = (Button) createAlbumView.findViewById(R.id.createAlbumButton);
            diaBuilder.setView(createAlbumView);
            final AlertDialog dialog = diaBuilder.create();
            dialog.show();
            createAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!albumName.getText().toString().isEmpty() && !albumDescription.getText().toString().isEmpty()) {
                        Album albumToCreate = new Album(albumName.getText().toString(), albumDescription.getText().toString());
                        albumRepo.createAlbumFromSelection(albumToCreate, AlbumsViewActivity.this);
                        dialog.dismiss();
                    }
                }
            });

        }

    }
}

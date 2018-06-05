package com.example.niephox.methophotos.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.GridView;
import android.widget.Toast;
import com.example.niephox.methophotos.Controllers.AlbumBuilder;
import com.example.niephox.methophotos.Controllers.AlbumRepository;
import com.example.niephox.methophotos.Controllers.FirebaseService;
import com.example.niephox.methophotos.Controllers.MetadataController;
import com.example.niephox.methophotos.Controllers.StorageAdapter;
import com.example.niephox.methophotos.ViewControllers.AlbumsAdapter;
import com.example.niephox.methophotos.ViewControllers.NavigationItemListener;
import com.example.niephox.methophotos.ViewControllers.GridSpacingItemDecoration;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumsViewActivity extends AppCompatActivity implements iAsyncCallback, View.OnClickListener {
    //ArrayLists:
    public ArrayList<Album> alAlbums = new ArrayList<>();

    //Layout Items:
    private DrawerLayout mdrawerLayout;
    //Controllers:
    FirebaseService firebaseService;
    private AlbumRepository albumRepo;
    MetadataController mtcontrol;
    private AlbumBuilder albumBuilder;
    AlbumBuilder.AsyncBuild AsalbumBuilder;
    //Intents:
    private User curentUser;
    private Album localAlbum;
    private AlbumRepository albumController;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    //Controllers:


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
        localAlbum = new Album("Local Photos", null, null, null);
        checkPermissions(AlbumsViewActivity.this);
        alAlbums.add(localAlbum);
        firebaseService.RegisterCallback(this);


    }

    public void setView() {
        initCollapsingToolbar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
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
        mdrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationItemListener(mdrawerLayout));

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
            case AUTOGENERATE:
                alAlbums.addAll(AsalbumBuilder.getAlbumscreated());
                adapter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public void RetrieveData(REQUEST_CODE rq) {
        if (rq == REQUEST_CODE.METADATA) {
            localAlbum = mtcontrol.album;
            alAlbums.set(0,localAlbum);
            adapter.notifyDataSetChanged();
        }
        else {
            curentUser = firebaseService.getUser();
            firebaseService.getUserAlbums();
            Log.e("alAlbums", alAlbums.size() + "");
            adapter.notifyDataSetChanged();
        }
    }

    //WRITTEN BY PETALIDIS:::::::::::::::::::::::::::::::::::::::
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path;
        List<String> imageURIs = new ArrayList<>();
        if (data != null) { //if user did not select anything
            if (data.getClipData() != null) { //if user selected more than one images, get the images from clipData
                for (int i = 0; i < data.getClipData().getItemCount(); i++)
                    imageURIs.add(StorageAdapter.getRealPathFromURI(this, data.getClipData().getItemAt(i).getUri()));
            } else
                imageURIs.add(StorageAdapter.getRealPathFromURI(this, data.getData())); //if data is not null and theres only one image selected just add the single image uri
        } else //if no Image is selected...
            return;

        //saves the selected images to the album that the repo is managing, and create an album simulteniously
        albumRepo.saveSelectedImages(imageURIs);
        //getting the album that has been created
        alAlbums.add(albumRepo.getAlbum());
        curentUser.addAlbums(alAlbums);
        adapter.notifyDataSetChanged();
        //curentUser.albumsClear();
        // albumRepo.transferImage(localAlbum.getImages().get(4), curentUser.getAlbums().get(0), curentUser.getAlbums().get(1));
        //albumRepo.deleteAlbum(curentUser.getAlbums().get(0));
        //firebaseService.getCurrentUser();
        //firebaseService.addImageToAlbum(localAlbum.getImages().get(0), curentUser.getAlbums().get(1));
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
                AsalbumBuilder = new AlbumBuilder.AsyncBuild(getWindow().getDecorView().findViewById(android.R.id.content),this,localAlbum.getImages());
                AsalbumBuilder.RegisterCallback(this);
                AsalbumBuilder.execute(localAlbum.getImages());

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

            mtcontrol = new MetadataController(localAlbum,this);
            mtcontrol.RegisterCallback(this);
            mtcontrol.execute(localAlbum);
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
                    if (!albumName.getText().toString().isEmpty() && !albumDescription.getText().toString().isEmpty()) {
                        Album albumToCreate = new Album(albumName.getText().toString(), albumDescription.getText().toString());
                        albumRepo.createAlbumFromSelection(albumToCreate, AlbumsViewActivity.this);
                        dialog.dismiss();
                    }
                }
            });

        }

    }
}
package com.example.niephox.methophotos.Activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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

import com.example.niephox.methophotos.Controllers.AlbumBuilder;
import com.example.niephox.methophotos.Controllers.AlbumRepository;
import com.example.niephox.methophotos.Controllers.FirebaseService;
import com.example.niephox.methophotos.Controllers.MetadataController;
import com.example.niephox.methophotos.Controllers.StorageAdapter;
import com.example.niephox.methophotos.ViewControllers.AlbumsAdapter;
import com.example.niephox.methophotos.ViewControllers.NavigationItemListener;
import com.example.niephox.methophotos.ViewControllers.GridSpacingItemDecoration;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.User;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumsViewActivity extends AppCompatActivity implements iAsyncCallback, View.OnClickListener {
    //ArrayLists:
    public  ArrayList<Album> alAlbums;
    //Layout Items:
    private ViewHolder viewHolder ;
    //Controllers:
    FirebaseService firebaseService;
    private AlbumRepository albumRepo;
    private MetadataController mtcontrol;
    private AlbumBuilder AsalbumBuilder;
    //Intents:
    private User curentUser;
    private Album localAlbum;
    private AlbumsAdapter albumsAdapter;
    private AlertDialog.Builder diaBuilder;
    private AlertDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        alAlbums = new ArrayList<>();
        curentUser = new User();
        setContentView(R.layout.activity_album);

        viewHolder = new ViewHolder();

        albumRepo = new AlbumRepository(); //TODO: we create a repo object to manage albums
        localAlbum = albumRepo.generateLocalAlbum(this); //TODO: we generate a local album using the albumRepo

        //AUTOMATIC GENERATION
        mtcontrol = new MetadataController(localAlbum,this);
        mtcontrol.RegisterCallback(this);
        mtcontrol.execute(localAlbum);

        setView();
        diaBuilder = new AlertDialog.Builder(this);
        diaBuilder.setView(viewHolder.createAlbumView);
        dialog = diaBuilder.create();

        firebaseService = new FirebaseService();
        firebaseService.RegisterCallback(this);

        firebaseService.getCurrentUser();

        alAlbums.add(localAlbum);
    }

    public void setView() {
        initCollapsingToolbar();
        albumsAdapter = new AlbumsAdapter(this, alAlbums);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        viewHolder.recyclerView.setLayoutManager(mLayoutManager);
        viewHolder.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        viewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        viewHolder.recyclerView.setAdapter(albumsAdapter);
        viewHolder.floatingActionButton.setOnClickListener(this);
        viewHolder.navigationView.setNavigationItemSelectedListener(new NavigationItemListener(viewHolder.mdrawerLayout,this));
    }

    @Override
    public void RefreshView(REQUEST_CODE rq) {
        switch (rq) {
            case STORAGE:
                alAlbums.clear(); //edited code alexander
                alAlbums.add(localAlbum);
                alAlbums.addAll(curentUser.getAlbums());
                albumsAdapter.notifyDataSetChanged();
                break;
            case DATABASE:
                break;
            case AUTOGENERATE:
                alAlbums.clear();
                alAlbums.addAll(AsalbumBuilder.getAlbumscreated());
                albumsAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void RetrieveData(REQUEST_CODE rq) {
        if (rq == REQUEST_CODE.METADATA) {
            localAlbum = mtcontrol.album;
            alAlbums.set(0,localAlbum);
            albumsAdapter.notifyDataSetChanged();
        }
        else {
            curentUser = new User();
            curentUser = firebaseService.getUser();
            firebaseService.getUserAlbums();
            Log.e("alAlbums", alAlbums.size() + "");
            albumsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
//		albumRepo.transferImage(localAlbum.getImages().get(0), alAlbums.get(0), alAlbums.get(1));
        alAlbums.add(albumRepo.getAlbum());
        curentUser.addAlbums(alAlbums);
        albumsAdapter.notifyDataSetChanged();
        //curentUser.albumsClear();
        // albumRepo.transferImage(localAlbum.getImages().get(4), curentUser.getAlbums().get(0), curentUser.getAlbums().get(1));
        //albumRepo.deleteAlbum(curentUser.getAlbums().get(0));
        //firebaseService.getCurrentUser();
        //firebaseService.addImageToAlbum(localAlbum.getImages().get(0), curentUser.getAlbums().get(1));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AsalbumBuilder = new AlbumBuilder(getWindow().getDecorView().findViewById(android.R.id.content),this);
                AsalbumBuilder.RegisterCallback(this);
                AsalbumBuilder.execute(localAlbum.getImages());

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCollapsingToolbar() {
        viewHolder.appBarLayout.setExpanded(true);
        // hiding & showing the title when toolbar expanded & collapsed
        viewHolder.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    viewHolder.collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    viewHolder.collapsingToolbar.setTitle(" ");
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
            albumRepo = new AlbumRepository();
           // diaBuilder.setView(viewHolder.createAlbumView);
//            dialog.hide();
            viewHolder.albumName.getText().clear();
            viewHolder.albumDescription.getText().clear();
            dialog.show();
            viewHolder.createAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!viewHolder.albumName.getText().toString().isEmpty() && !viewHolder.albumDescription.getText().toString().isEmpty()) {
                        Album albumToCreate = new Album(viewHolder.albumName.getText().toString(), viewHolder.albumDescription.getText().toString());
                        albumRepo.createAlbumFromSelection(albumToCreate, AlbumsViewActivity.this);
                        dialog.dismiss();
                    }
                }

            });

        }

    }
    private class ViewHolder {
        final DrawerLayout mdrawerLayout;
        final RecyclerView recyclerView;
        final Toolbar toolbar;
        final ActionBar actionBar;
        final FloatingActionButton floatingActionButton;
        final NavigationView navigationView;
        final CollapsingToolbarLayout collapsingToolbar;
        final AppBarLayout appBarLayout;
        final View createAlbumView;
        final EditText albumName;
        final EditText albumDescription;
        final Button createAlbum;

        ViewHolder(){
            mdrawerLayout = findViewById(R.id.drawer_layout);
            recyclerView = findViewById(R.id.recycler_view);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            floatingActionButton = findViewById(R.id.addAlbum);
            navigationView = findViewById(R.id.navigation);
            collapsingToolbar = findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(" ");
            appBarLayout = findViewById(R.id.appbar);
            createAlbumView = getLayoutInflater().inflate(R.layout.layout_create_album, null);
            albumName = createAlbumView.findViewById(R.id.albumName);
            albumDescription = createAlbumView.findViewById(R.id.albumDescription);
            createAlbum = createAlbumView.findViewById(R.id.createAlbumButton);
        }
    }
}
//TODO: TESTING
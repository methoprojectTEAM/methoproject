package com.example.niephox.methophotos.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niephox.methophotos.Controllers.MetadataControllers.MetadataController;

import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;
import com.example.niephox.methophotos.ViewControllers.MetadataViewController;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;




import java.util.ArrayList;



public class MetadataActivity extends AppCompatActivity implements iAsyncCallback, View.OnClickListener {

    ImageView ivImage;
    ListView lvMetadata;
    TextView tvName;
    TextView tvAlbumName;
    TextView tvDate;
    Button btTags;

    public static ArrayAdapter<String> arrayAdapter;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ArrayList<String> metadataList = new ArrayList<String>();
    public MetadataController metadataController;
    private MetadataViewController metadataViewController;
    private Image image;
    private String[] listitems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metadata);

        InitialiseViews();
        GetImage();
        metadataController = new MetadataController(image);
        metadataViewController = new MetadataViewController(metadataController);
        metadataController.RegisterCallback(this);
        SetViews();
        metadataViewController.ReaderAlertDialog(this);

    }

    private void InitialiseViews() {
        lvMetadata = findViewById(R.id.lvMetadata);
        ivImage = findViewById(R.id.imgMetadata);
        listitems = getResources().getStringArray(R.array.readers_list);
        tvAlbumName = findViewById(R.id.tvAlbumName);
        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        btTags = findViewById(R.id.btTags);
        metadataList.add("will soon be filled");
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                metadataList);
        lvMetadata.setAdapter(arrayAdapter);
        btTags.setOnClickListener(this);
    }

    private void SetViews() {
        ShowImage();
        tvName.setText(image.getName());
//        if (image.getName()!=null && image.getAlbum().getName() !=null) {
//            tvName.setText(image.getName());
//            //tvAlbumName.setText(image.getAlbum().getName());
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btTags:
                metadataViewController.TagAlertDialog(this);
                break;
            default:
                break;
        }
    }

    private void ShowImage() {
        if (image.getDownloadUrl() == null) {
            Glide
                    .with(this)
                    .load(image.getImageURI())
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(ivImage);
        } else {
            Glide
                    .with(this)
                    .load(image.getDownloadUrl())
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(ivImage);
        }
    }

    private void GetImage() {
        image=getIntent().getParcelableExtra("image");
    }

    @Override
    public void RefreshView(REQUEST_CODE rq) {
        if(rq == REQUEST_CODE.METADATA ){
        metadataList.clear();
        metadataList.addAll(MetadataController.filteredList);
        toast(" " + metadataList.size());
        arrayAdapter.notifyDataSetChanged();}
    }

    @Override
    public void RetrieveData(REQUEST_CODE rq) {

    }

    public void toast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}



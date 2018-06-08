package com.example.niephox.methophotos.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.ViewControllers.AlbumsAdapter;
import com.example.niephox.methophotos.ViewControllers.PhotosGridViewAdapter;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;

/**
 * Created by IgorSpiridonov
 */


public class PhotosViewActivity extends AppCompatActivity {

    //Layout Items:
    GridView gvImages;

    //Adapters:
    PhotosGridViewAdapter albumsAdapter;

    //Array Lists:
    ArrayList<Image> alImages = new ArrayList<>();

    //Current album name
	String albumName;
	String[] userAlbumsNames;
	private AlertDialog.Builder diaBuilder;
	private AlertDialog dialog;

	private View editCommentsView;
	private EditText commentsEditText;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view_layout);

        editCommentsView = getLayoutInflater().inflate(R.layout.layout_edit_comments,null);

        diaBuilder = new AlertDialog.Builder(this);

        diaBuilder.setView(editCommentsView);
        gvImages = findViewById(R.id.gvImages);
		dialog = diaBuilder.create();
		commentsEditText = editCommentsView.findViewById(R.id.commentsEditText);


        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//
//        if(bundle != null){
//            alImages=(ArrayList<Image>) bundle.getSerializable("alImages");
//        }
		albumName = intent.getStringExtra("albumName");
        alImages=intent.getParcelableArrayListExtra("alImages");
        userAlbumsNames = intent.getStringArrayExtra("userAlbumsNames");

        albumsAdapter = new PhotosGridViewAdapter(this, alImages, userAlbumsNames,dialog,commentsEditText,editCommentsView);
        gvImages.setAdapter(albumsAdapter);

        gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(PhotosViewActivity.this,MetadataActivity.class);
                intent.putExtra("image",alImages.get(position));
                startActivity(intent);
            }
        });


    	gvImages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
				commentsEditText.setText(alImages.get(position).getDescription());
				albumsAdapter.showPopupMenu(view, position, albumName);
				return false;
			}
		});



    }

}

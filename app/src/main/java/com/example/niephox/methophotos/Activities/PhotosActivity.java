package com.example.niephox.methophotos.Activities;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.example.niephox.methophotos.ViewControllers.GridViewAdapter;
import com.example.niephox.methophotos.R;


public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView)findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this,MainActivity.al_images,int_position);
        gridView.setAdapter(adapter);
    }
}

package com.example.niephox.methophotos.extractor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;

public class MetadataActivity extends AppCompatActivity {

    ImageView image;
    Button btExit;
    TextView textView;
    ListView lvMetadata;
    ArrayList<String> metadataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metadata);

        lvMetadata =  findViewById(R.id.lvMetadata);
        image = findViewById(R.id.imgMetadata);

        Iterable<JpegSegmentMetadataReader> readers = null;
        MetadataExtractor.DownloadFileAndExtractMetadata("rainforest.jpg",readers);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                metadataList );

        lvMetadata.setAdapter(arrayAdapter);
    }
}

package com.example.niephox.methophotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.BufferedInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.net.URI;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageView targetImage;
    ImageView targetImage2;
    TextView textTest;
    Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addPhotoButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.buttonDelete);

        targetImage = (ImageView)findViewById(R.id.photoView);
        targetImage2=findViewById(R.id.photoView2);
        textTest=findViewById(R.id.textView);

        addPhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        targetImage2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    InputStream is = getContentResolver().openInputStream(path);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Metadata metadata = ImageMetadataReader.readMetadata(bis,true);
//                    Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(path)), path.length);



                    for (Directory directory : metadata.getDirectories()) {
                        for (Tag tag : directory.getTags()) {
                            System.out.println(tag);
                            textTest.setText(tag.toString());
                        }
                    }

                }
                catch (ImageProcessingException e){textTest.setText("ERROR 1");}
                catch (IOException e) {textTest.setText("ERROR 2");}
            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            path =targetUri;
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                if(requestCode==0)
                    targetImage.setImageBitmap(bitmap);
                else
                    targetImage2.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}


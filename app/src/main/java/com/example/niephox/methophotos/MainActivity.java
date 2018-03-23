package com.example.niephox.methophotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.BufferedInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    ImageView[] targetImage=new ImageView[6];
    Uri[] path=new Uri[6];
    int numberPhotos=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addPhotoButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.buttonDelete);

        targetImage[0] = findViewById(R.id.photoView1);
        targetImage[1] = findViewById(R.id.photoView2);
        targetImage[2] = findViewById(R.id.photoView3);
        targetImage[3] = findViewById(R.id.photoView4);
        targetImage[4] = findViewById(R.id.photoView5);
        targetImage[5] = findViewById(R.id.photoView6);



        addPhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        targetImage[0].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),PhotoViewActivity.class);
                i.setData(path[0]);
                startActivity(i);
            }
        });
        targetImage[1].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),PhotoViewActivity.class);
                i.setData(path[1]);
                startActivity(i);
            }
        });
        targetImage[2].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),PhotoViewActivity.class);
                i.setData(path[2]);
                startActivity(i);
            }
        });
        targetImage[3].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),PhotoViewActivity.class);
                i.setData(path[3]);
                startActivity(i);
            }
        });
        targetImage[4].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),PhotoViewActivity.class);
                i.setData(path[4]);
                startActivity(i);
            }
        });
        targetImage[5].setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),PhotoViewActivity.class);
                i.setData(path[5]);
                startActivity(i);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    InputStream is = getContentResolver().openInputStream(path[0]);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Metadata metadata = ImageMetadataReader.readMetadata(bis,true);

                    for (Directory directory : metadata.getDirectories()) {
                        for (Tag tag : directory.getTags()) {
                            System.out.println(tag);
                        }
                    }
                }
                catch (ImageProcessingException e){System.out.println("ERROR");}
                catch (IOException e) {System.out.println("error");}
            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();

            path[numberPhotos] =targetUri;
            Bitmap bitmap;

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage[numberPhotos].setImageBitmap(bitmap);
                setBackgroundColor();
                numberPhotos++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public void setBackgroundColor()
    {
        targetImage[numberPhotos].setBackgroundColor(Color.WHITE);
    }
}


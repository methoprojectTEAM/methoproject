package com.example.niephox.methophotos.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlbumBuilder {
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Album> albumscreated = new ArrayList<>();
    private MetadataController metadataController = new MetadataController();
    private iAsyncCallback iAsyncCallback;
    private ArrayList<String> metadataString = new ArrayList<>();
    private Context context;
    private String resp;
    boolean flag = false;
    ProgressDialog progressDialog;

    public AlbumBuilder(){
    }


    public   ArrayList<Album> buildBasedOnDate(ArrayList<Image> localImages) {
        //images.clear();
        images.addAll(localImages);
        String dateTag = "[File] File Modified Date";
        for (int i = 0; i < 20; i++) {
            metadataController.ExtractMetadata(images.get(i));
            metadataString.addAll(metadataController.filteredList);
            for (String tag : metadataString) {
                if (tag.contains(dateTag)) {
                    String[] tagSplit = tag.split("- ", 2);
                    Date date = dateParser(tagSplit[1]);
                    calculateAlbum(date,images.get(i));
                }
            }
        }
        flag = true;
        return albumscreated;
    }

    private void calculateAlbum(Date date,Image image) {
        if(albumscreated.size()== 0){
            newAlbumCreation(date);
        }
        for (Album albumChild: albumscreated){
            if (albumChild.getDate() == date){
                albumChild.addImage(image);
            }else{
                newAlbumCreation(date);
            }
            break;
        }

    }
    private void newAlbumCreation(Date date){
        Album album = new Album();
        album.setName(date.toString());
        album.setDescription("Automatically created based on date");
        album.setDate(date);
        albumscreated.add(album);
    }


    private Date dateParser(String input) {
        SimpleDateFormat parser = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        Date date = new Date();
        try {
            date = parser.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String FormattedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("DD MM YYYY");
        FormattedDate = formatter.format(date);
        try {
              Date newdate = formatter.parse(FormattedDate);
              date =newdate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

}

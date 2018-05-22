package com.example.niephox.methophotos.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class AlbumBuilder {
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Album> albumscreated = new ArrayList<>();
    private MetadataController metadataController = new MetadataController();
    public static iAsyncCallback iAsyncCallback;
    private ArrayList<String> metadataString = new ArrayList<>();
    private Context context;
    private String resp;
    boolean flag = false;
    ProgressDialog progressDialog;

    public AlbumBuilder(){
    }
    public  ArrayList<Album> getAlbumsGenerated (){
        return this.albumscreated;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public   void buildBasedOnDate(ArrayList<Image> localImages) {
        //images.clear();
        images.addAll(localImages);
        String dateTag = "[File] File Modified Date";
        int counter = 0;
        for (int i = 0; i < 50; i++) {
            metadataController.ExtractMetadata(images.get(i));
            metadataString.clear();
            metadataString.addAll(metadataController.filteredList);

            for (String tag : metadataString) {
                if (tag.contains(dateTag)) {

                    counter++;
                    String[] tagSplit = tag.split("- ", 2);
                    Date date = dateParser(tagSplit[1]);
                    calculateAlbum(date,images.get(i));

                }
            }
            Log.w("IMGTAG","image "+i + "has iffed "+counter);
            counter = 0;
        }

        iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.METADATA);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateAlbum(Date date, Image image) {
        if(albumscreated.size()== 0){
            newAlbumCreation(date,image);
        }
        flag = false;
        for (Album albumChild: albumscreated){

            LocalDate albumdate = albumChild.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate imageDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
           long timedif = ChronoUnit.DAYS.between(albumdate,imageDate);

           if (ChronoUnit.DAYS.between(albumdate,imageDate) == 0) {
                albumChild.addImage(image);
                flag = true;
                break;
            }
        }
        if(flag == false){
            newAlbumCreation(date,image);

        }


    }
    private void newAlbumCreation(Date date, Image image){
        Album album = new Album();
        album.setName(date.toString());
        album.setThumbnail(image);
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



        return date;
    }
    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
    }


}

package com.example.niephox.methophotos.Controllers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;
import com.example.niephox.methophotos.ViewControllers.InfoBottomDialog;

import java.lang.reflect.Array;
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

    public AlbumBuilder() {
    }

    public ArrayList<Album> getAlbumsGenerated() {
        return this.albumscreated;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buildBasedOnDate(ArrayList<Image> localImages) {
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
                    calculateAlbum(date, images.get(i));

                }
            }
            Log.w("IMGTAG", "image " + i + "has iffed " + counter);
            counter = 0;
        }

        iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.METADATA);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateAlbum(Date date, Image image) {
        if (albumscreated.size() == 0) {
            newAlbumCreation(date, image);
        }
        flag = false;
        for (Album albumChild : albumscreated) {

            LocalDate albumdate = albumChild.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate imageDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long timedif = ChronoUnit.DAYS.between(albumdate, imageDate);

            if (ChronoUnit.DAYS.between(albumdate, imageDate) == 0) {
                albumChild.addImage(image);
                flag = true;
                break;
            }
        }
        if (flag == false) {
            newAlbumCreation(date, image);

        }


    }


    private void newAlbumCreation(Date date, Image image) {
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

    public static class AsyncBuild extends AsyncTask<String, Integer, String> {

        private View rootView;
        private Context context;
        private InfoBottomDialog infoBottomDialog;
        private ProgressBar progressBar;

        public AsyncBuild(View rootView, Context context) {
            this.rootView = rootView;
            this.context = context;
        }

        /**
         * ASYNCTASK <PARAMS,PROGRESS, RESULT>
         *
         * @params = this  is the object you pass to the async task from .execute
         * @PROGRESS= this is the  type that gets pass to on progressUpdate()
         * @RESULT =the type that returns from doInBackground()
         */

        //Runs  in  UI before  background thread is called.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Snackbar snackbar = Snackbar.make(rootView, "FML?", Snackbar.LENGTH_LONG);
            snackbar.show();
            progressBar = rootView.findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.VISIBLE);
            BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(rootView.findViewById(R.id.Sheet));
            sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
//            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
//            infoBottomDialog = InfoBottomDialog.newInstance();
//            infoBottomDialog.show(fragmentManager, "add diallog");



            // Display Snackbar
        }

        //This is the background thread
        @Override
        protected String doInBackground(String... strings) {
            // get the string form params , which is an ARRAY
            String myString = strings[0];
            //Do something that takes a long time
            for (int i = 0; i <= 100; i++) {
                //DO STUFF
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Update progress
                publishProgress(i);
            }

            //Return something passed on post execute
            return "STRING PASSED ON POST EXECUTE";

        }

        //This is called from the background thread but  runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //infoBottomDialog.updateProgress(values[0] + 1);
            //update progressBar
            progressBar.setProgress(values[0]+1);
        }

        //This  runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Snackbar snackbar = Snackbar.make(rootView, "Done", Snackbar.LENGTH_LONG);
            snackbar.show();
            //Hide progress bar  and make the changes
        }
    }
}

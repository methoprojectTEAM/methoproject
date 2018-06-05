package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;
import com.example.niephox.methophotos.ViewControllers.InfoBottomDialog;

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
    boolean flag = false;


    public AlbumBuilder(ArrayList<Image> inputImages) {
        this.images = inputImages;


    }



//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void buildBasedOnDate(ArrayList<Image> localImages) {
//        //images.clear();
//        images.addAll(localImages);
//        String dateTag = "[File] File Modified Date";
//        int counter = 0;
//        for (int i = 0; i < 50; i++) {
//            metadataController.ExtractMetadata(images.get(i));
//            metadataString.clear();
//            metadataString.addAll(metadataController.metadataList);
//
//            for (String tag : metadataString) {
//                if (tag.contains(dateTag)) {
//
//                    counter++;
//                    String[] tagSplit = tag.split("- ", 2);
//                    // Date date = dateParser(tagSplit[1]);
//                    // calculateAlbum(date, images.get(i));
//
//                }
//            }
//            Log.w("IMGTAG", "image " + i + "has iffed " + counter);
//            counter = 0;
//        }
//
//        iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.METADATA);
//
//    }


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


    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
    }

    public static class AsyncBuild extends AsyncTask<ArrayList<Image>, Integer, String> {

        /**
         * ASYNCTASK <PARAMS,PROGRESS, RESULT>
         *
         * @params = this  is the object you pass to the async task from .execute
         * @PROGRESS= this is the  type that gets pass to on progressUpdate()
         * @RESULT =the type that returns from doInBackground()
         */


        private MetadataController metadataController = new MetadataController();
        private View rootView;
        private Context context;
        private InfoBottomDialog infoBottomDialog;
        private ProgressBar progressBar;
        private ArrayList<String> metadataString = new ArrayList<>();
        private ArrayList<Image> images = new ArrayList<>();
        public static iAsyncCallback iAsyncCallback;

        public ArrayList<Album> getAlbumscreated() {
            return albumscreated;
        }
        public void RegisterCallback(iAsyncCallback iAsyncCallback) {
            this.iAsyncCallback = iAsyncCallback;
        }
        private ArrayList<Album> albumscreated = new ArrayList<>();

        public AsyncBuild(View rootView, Context context, ArrayList<Image> inputImages) {
            this.rootView = rootView;
            this.context = context;
            this.images = inputImages;
        }

        //Runs  in  UI before  background thread is called.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Snackbar snackbar = Snackbar.make(rootView, "FML?", Snackbar.LENGTH_LONG);
            snackbar.show();
            progressBar = rootView.findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(images.size());
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

        @Override
        protected String doInBackground(ArrayList<Image>... inputImages) {
            ArrayList<Image> images = new ArrayList<>();
            images.addAll(inputImages[0]);

            //For each image get  metadata and sort into the correct album
            for (int i = 0; i < images.size(); i++) {
                metadataString.clear();
                if(images.get(i).getMetadata()!= null){
                    metadataString.addAll(images.get(i).getMetadata());
                    calculateAlbumBase(metadataString,"Date",images.get(i));
                }
                publishProgress(i);
            }
            return "Done";
        }


        //This is called from the background thread but  runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //infoBottomDialog.updateProgress(values[0] + 1);
            //update progressBar
            progressBar.setProgress(values[0] + 1);
        }


        //This  runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Snackbar snackbar = Snackbar.make(rootView, "Done", Snackbar.LENGTH_LONG);
            snackbar.show();
            iAsyncCallback.RefreshView(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.AUTOGENERATE);
            //Hide progress bar  and make the changes
        }
        private void calculateAlbumBase(ArrayList<String> metadataString, String baseTag, Image image) {
            switch (baseTag) {
                case "Date":
                    calculateByDate(metadataString, image);
                    break;
                default:
                    break;
            }
        }

        private Date parseDate(ArrayList<String> metadataString) {
            String dateTag = "[Exif IFD0] Date/Time";
            for (String tag : metadataString) {
                if (tag.contains(dateTag)) {
                    String[] tagSplit = tag.split("- ", 2);
                    Date date = dateParser(tagSplit[1]);
                    return date;
                }
            }
            return null;
        }

        private Date dateParser(String input) {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            Date date = new Date();
            try {
                date = parser.parse(input);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        private void newAlbumByDate(Date imageDate, Image image) {
            Album album = new Album();
            ArrayList<Image> images = new ArrayList<>();
            images.add(image);
            album.setImages(images);
            album.setName("a week starting from "+ imageDate.toString());
            album.setThumbnail(image);
            album.setDescription("Automatically created based on date");
            album.setDate(imageDate);
            albumscreated.add(album);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void calculateByDate(ArrayList<String> metadataString, Image image) {
            Date imageDate = parseDate(metadataString);


            if (imageDate != null) {
                if (albumscreated.size() == 0) {
                    newAlbumByDate(imageDate, image);
                } else {
                    boolean albumExists = false;
                    int albumIndex = 0;
                    for (int i = 0; i < albumscreated.size(); i++) {
                        LocalDate imageDateLocal = imageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate albumdate = albumscreated.get(i).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        long timedif = ChronoUnit.DAYS.between(albumdate, imageDateLocal);
                        if (timedif <= 8 || timedif == 0) {
                            albumExists = true;
                            albumIndex = i;
                        }
                        if (albumExists) {
                            albumscreated.get(albumIndex).addImage(image);
                            break;
                        } else {
                            newAlbumByDate(imageDate, image);
                            break;
                        }
                    }
                }
            }
        }
    }


}

package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
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


public class AlbumBuilder extends AsyncTask<ArrayList<Image>, Integer, String> {

    public static iAsyncCallback iAsyncCallback;
    /**
     * ASYNCTASK <PARAMS,PROGRESS, RESULT>
     *
     * @params = this  is the object you pass to the async task from .execute
     * @PROGRESS= this is the  type that gets pass to on progressUpdate()
     * @RESULT =the type that returns from doInBackground()
     */
    private int Imagesfailed = 0;
    private MetadataController metadataController = new MetadataController();
    private View rootView;
    private Context context;
    private InfoBottomDialog infoBottomDialog;
    private ProgressBar progressBar;
    private ArrayList<String> metadataString = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Album> albumscreated = new ArrayList<>();

    public AlbumBuilder(View rootView, Context context, ArrayList<Image> inputImages) {
        this.rootView = rootView;
        this.context = context;
        this.images = inputImages;
    }

    public ArrayList<Album> getAlbumscreated() {
        return albumscreated;
    }

    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(ArrayList<Image>... inputImages) {
        ///ArrayList<Image> images = new ArrayList<>();
        //images.addAll(inputImages[0]);

        //For each image get  metadata and sort into the correct album
//            for (int i = 0; i < this.images.size(); i++) {
        int progress = 0;
        while (this.images.size() != 0) {
            metadataString.clear();
            if (this.images.get(0).getMetadata() != null) {
                metadataString.addAll(this.images.get(0).getMetadata());
                calculateAlbumBase(metadataString, "Date");
                this.images.remove(0);
            }
            progress++;
            publishProgress(progress);
        }
        return "Done . Images failed to process " + Imagesfailed;
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
        Snackbar snackbar = Snackbar.make(rootView, s, Snackbar.LENGTH_LONG);
        snackbar.show();
        iAsyncCallback.RefreshView(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.AUTOGENERATE);
        //Hide progress bar  and make the changes
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateAlbumBase(ArrayList<String> metadataString, String baseTag) {
        switch (baseTag) {
            case "Date":
                calculateByDate(metadataString);
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
        album.setName("a week starting from " + imageDate.toString());
        album.setThumbnail(image);
        album.setDescription("Automatically created based on date");
        album.setDate(imageDate);
        albumscreated.add(album);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateByDate(ArrayList<String> metadataString) {
        Date imageDate = parseDate(metadataString);
        boolean albumExists = false;
        int albumIndex = 0;
        if (albumscreated.size() == 0) {
            newAlbumByDate(imageDate, this.images.get(0));
        }
        if (imageDate != null) {
            for (int i = 0; i < albumscreated.size(); i++) {
                albumExists = false;
                albumIndex = 0;
                LocalDate imageDateLocal = imageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate albumdate = albumscreated.get(i).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long timedif = ChronoUnit.DAYS.between(albumdate, imageDateLocal);
                if (timedif <= 8 || timedif == 0) {
                    albumExists = true;
                    albumIndex = i;
                    break;
                }
            }
            if (albumExists) {
                albumscreated.get(albumIndex).addImage(this.images.get(0));
            } else {
                newAlbumByDate(imageDate, this.images.get(0));
            }
        } else {
            Imagesfailed++; // didnt have the tag
        }
    }
}


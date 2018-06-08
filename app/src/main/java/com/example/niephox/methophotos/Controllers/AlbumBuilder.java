package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.drew.lang.GeoLocation;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;
import com.example.niephox.methophotos.ViewControllers.InfoBottomDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class and functionality is still at its alpha version
 * TODO: Implement correct call
 * TODO: Addition of more sorting Bases. (Color, Location...)
 * TODO: Refactor to the point that the whole  AAG algorithm does not depend on a specific sort base
 * TODO: Polish and give  efficient UX
 */
public class AlbumBuilder extends AsyncTask<ArrayList<Image>, Integer, String> {
    /**
     * Automatic Album Generation expects that the images imported for album calculation  already have their metadata set and stored in their object.
     * If an image doesn't have its metadata extracted then it will not be concluded in the AAG
     *
     * @param Imagesfailed : integer variable counting the images that  didn't have the corresponding tag for the AAG
     * @param rootView : Activity view variable used for displaying UI for best UX according the background AAG progress
     * @param context : Same as the above
     * @param progressBar infoBottomDialog : both  elements serving the UX
     * @param metadataString : Array list  temporarily containing String  references of the current  image obj on calculation
     * @param albumsCreated : Array list containing  albums created by AAG
     */
    public static iAsyncCallback iAsyncCallback;
    private int Imagesfailed = 0;
    private View rootView;
    private Context context;
    private InfoBottomDialog infoBottomDialog;
    private ProgressBar progressBar;
    private ArrayList<String> metadataString = new ArrayList<>();
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Album> albumscreated = new ArrayList<>();
    private AAG_BASE base;
    private Boolean initialized;

    public AlbumBuilder(View rootView, Context context, AAG_BASE base) {
        this.rootView = rootView;
        this.context = context;
        this.base = base;
    }

    public ArrayList<Album> getAlbumscreated() {
        return albumscreated;
    }

    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
    }

    /**
     * ASYNCTASK <PARAMS,PROGRESS, RESULT>
     *
     * @params = this  is the object you pass to the async task from .execute
     * @PROGRESS= this is the  type that gets pass to on progressUpdate()
     * @RESULT =the type that returns from doInBackground()
     */
    //Runs  in  UI before  background thread is called.
    //Inform the user that the automatic generation of albums has begun.
    //Set the  according progress bars etc to show the process of the task whenever the user asks.
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
        // FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        //infoBottomDialog = InfoBottomDialog.newInstance();
        //infoBottomDialog.show(fragmentManager, "add diallog");
        // Display Snackbar
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(ArrayList<Image>... inputImages) {
        //Use input images as images.
        this.images = inputImages[0];
        int progress = 0;
        initialized = false;
        while (this.images.size() != 0) {
            metadataString.clear();
            if (this.images.get(0).getInfoMap() != null) {
                calculateAlbumBase();
                this.images.remove(0);
            } else {
                Imagesfailed++;
                this.images.remove(0);
            }
            progress++;
            publishProgress(progress);
        }
        Finalise();
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

    /**
     * ASYNC TASK
     */


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initialiseAAG() {
        switch (this.base) {
            case DATE:
                basedOnDateInitialization();
                break;
            case LOCATION:
                basedOnLocationInitiation();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateAlbumBase() {
        switch (this.base) {
            case DATE:
                calculateAlbum();
                break;
            case LOCATION:
                calculateAlbum();
                break;
            default:
                break;
        }
    }

    //Main Algorithm to calculate albums
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateAlbum() {
        boolean albumExists = false;
        int albumIndex = 0;
        if(albumscreated.size() == 0 && !initialized){
            initialiseAAG();
        }else {
            for (int i = 0; i < albumscreated.size(); i++) {
                albumExists = false;
                albumIndex = 0;
                if (calculateBaseResult(albumscreated.get(i))) {
                    albumExists = true;
                    albumIndex = i;
                    break;
                }
            }
            if (albumExists) {
                albumscreated.get(albumIndex).addImage(this.images.get(0));
            } else {
                createAlbumOnBase();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createAlbumOnBase() {
        switch (this.base) {
            case DATE:
                newAlbumByDate(this.images.get(0));
                break;
            case LOCATION:
                newAlbumByLocation(this.images.get(0));
                break;
        }
    }

    private void Finalise() {
        switch (this.base) {
            case DATE:
                dateFinalisation();
                break;
            case LOCATION:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean calculateBaseResult(Album album) {
        boolean result = false;
        switch (this.base) {
            case DATE:
                result = DateBase(this.images.get(0), album);
                break;
            case LOCATION:
                result = locationBase(this.images.get(0),album);
        }
        return result;
    }
    /**
     * Methods used to calculate albums by date
     * */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void basedOnDateInitialization() {
        if(this.images.get(0).getInfoMap().get("Date") != null){
        newAlbumByDate(this.images.get(0));
        initialized = true;
        }else{
            initialized = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void newAlbumByDate(Image image) {
        Album album = new Album();
        ArrayList<Image> images = new ArrayList<>();
        images.add(image);
        Date imageDate = (Date) image.getInfoMap().get("Date");
        String fromDate = imageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();
        String todate = imageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();
        album.setImages(images);
        album.setName("My photos between " + fromDate + " and " + todate + " ");
        album.setThumbnail(image);
        album.setDescription("Automatically generated based on date");
        album.setDate(Calendar.getInstance().getTime());
        albumscreated.add(album);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean DateBase(Image image, Album album) {
        boolean result;
        Date imageDate = (Date) image.getInfoMap().get("Date");
        LocalDate imageDateLocal = imageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate albumdate = album.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long timediff = ChronoUnit.DAYS.between(albumdate, imageDateLocal);

        if (timediff <= 8 || timediff == 0) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    private void dateFinalisation() {
        Collections.sort(this.albumscreated);
    }

    /**
     * Methods to calculate albums based on Location
     *
     * */
    private void basedOnLocationInitiation(){
        if(this.images.get(0).getInfoMap().get("Location") != null){
            newAlbumByLocation(this.images.get(0));
            initialized = true;
        }else{
            initialized = false;
        }
    }

    private void newAlbumByLocation(Image image){
        Album album = new Album();
        ArrayList<Image> images = new ArrayList<>();
        images.add(image);
        GeoLocation imageLocation = (GeoLocation) image.getInfoMap().get("Location");
        String[] imageCity = getLocationFromCoordinates(new LatLng(imageLocation.getLatitude(),imageLocation.getLongitude()));
        album.setImages(images);
        album.setName("My photos in "+ imageCity[0] );
        album.setThumbnail(image);
        album.setDescription("Automatically generated based on Location");
        album.setDate(Calendar.getInstance().getTime());
        albumscreated.add(album);
    }

    private boolean locationBase (Image image, Album album){
        boolean result;
        GeoLocation imageLocation = (GeoLocation) image.getInfoMap().get("Location");
        String imageCity = getLocationFromCoordinates(new LatLng(imageLocation.getLatitude(),imageLocation.getLongitude()))[0];


        GeoLocation albumLocation = (GeoLocation) album.getImages().get(0).getInfoMap().get("Location");
        String albumCity = getLocationFromCoordinates(new LatLng(albumLocation.getLatitude(),albumLocation.getLongitude()))[0];
        if(imageCity.equals(albumCity)){
            result = true;
        }else{
            result = false;
        }
        return result;
    }


    public String[] getLocationFromCoordinates(final LatLng coord) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String location[] = new String[2];
        try {
            List<Address> addresses = geocoder.getFromLocation(coord.latitude, coord.longitude, 2);
            location[0] = addresses.get(0).getLocality();
            location[1] = addresses.get(0).getCountryName();
        } catch (Exception ex) {
            location[0] = "NoCity";
            location[1] = "NoCountry";
        }
        return location;
    }


    public enum AAG_BASE {
        DATE,
        LOCATION
    }
}


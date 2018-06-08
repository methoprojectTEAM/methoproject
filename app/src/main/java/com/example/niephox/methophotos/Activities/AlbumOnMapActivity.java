package com.example.niephox.methophotos.Activities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niephox.methophotos.CustomListeners.CustomMapRequestListener;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.InfoWindowData;
import com.example.niephox.methophotos.R;
import com.example.niephox.methophotos.ViewControllers.MapAdapters.CustomMapMarkerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AlbumOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<Image> imagesToShow = new ArrayList<>();
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        geocoder = new Geocoder(this, Locale.getDefault());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();

        imagesToShow = extras.getParcelableArrayList("alImages");
        for (Image imgToShow : imagesToShow) {
            if (imgToShow.getDownloadUrl() == null)
                urls.add(imgToShow.getImageURI());
            else
                urls.add(imgToShow.getDownloadUrl());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng cameraLoc = null;
        boolean hasCameraLocation = false;
        for (int i = 0; i < imagesToShow.size(); i++) {
            //LOCATION FROM METADATA
            if (imagesToShow.get(i).getParcableLocation() != null) {
                addCustomMarker(AlbumOnMapActivity.this, imagesToShow.get(i).getParcableLocation(), urls.get(i));
                if (!hasCameraLocation) {
                    cameraLoc = imagesToShow.get(i).getParcableLocation();
                    hasCameraLocation = true;
                }
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraLoc));
    }

    public void addCustomMarker(final Context context, final LatLng coord, final String url) {
        final View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        ImageView markerImage = marker.findViewById(R.id.markerImageView);

        String[] imageLocation = getLocationFromCoordinates(coord);
        InfoWindowData info = new InfoWindowData(url, imageLocation[0], imageLocation[1], "1/6/2018");
        CustomMapMarkerAdapter customMapMarkerAdapter = new CustomMapMarkerAdapter
                .Builder()
                .context(context)
                .map(mMap)
                .marker(marker)
                .coordinates(coord)
                .imageData(info)
                .build();

        Glide
                .with(context)
                .load(url)
                .asBitmap()
                .thumbnail(0.5f)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .listener(new CustomMapRequestListener(customMapMarkerAdapter)) //When Glide loads the image then create the new custom marker
                .into(markerImage);
    }

    /**
     * @param coord Takes the coordinates as LatLng Object
     * @return returns a two String table where the first String is the city and the second Country
     * @author IgorSpiridonov
     */
    public String[] getLocationFromCoordinates(final LatLng coord) {
        String location[] = new String[2];
        try {
            List<Address> addresses = geocoder.getFromLocation(coord.latitude, coord.longitude, 1);
            location[0] = addresses.get(0).getAddressLine(1);
            location[1] = addresses.get(0).getCountryName();
        } catch (Exception ex) {
            location[0] = "NoCity";
            location[1] = "NoCountry";
        }
        return location;
    }
}

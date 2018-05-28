package com.example.niephox.methophotos.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.niephox.methophotos.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AlbumOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        refreshMarket(AlbumOnMapActivity.this,new LatLng(-34,151),"https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/xGwcgPWbqeV3QNo3xa0OdxsTGcf2%2F03075179-e451-4edb-acf4-16681b1878e0?alt=media&token=5d129561-d5a6-4768-a164-afde9d543cbf");
        refreshMarket(AlbumOnMapActivity.this,new LatLng(-54,131),"https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/xGwcgPWbqeV3QNo3xa0OdxsTGcf2%2Ffbeee3a6-369c-4bf0-8916-08643e80246f?alt=media&token=a0b9626b-7f47-4ed1-933a-77570051156b");
        refreshMarket(AlbumOnMapActivity.this,new LatLng(-74,111),"https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/xGwcgPWbqeV3QNo3xa0OdxsTGcf2%2Ffd470096-8833-40ab-80c5-cf6d9c5bf853?alt=media&token=e65bafc3-6e2a-4f09-90b1-b8a046295c63");
        refreshMarket(AlbumOnMapActivity.this,new LatLng(-84,91),"https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/luca-bravo-500474-unsplash.jpg?alt=media&token=ebbcfb4f-0a95-4dcd-80b4-2b11f0259414");

    }
    public Bitmap createCustomMarker(final Context context,View marker){





        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public void refreshMarket(Context context,final LatLng coord,String url){
        final View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        ImageView markerImage = (ImageView) marker.findViewById(R.id.markerImageView);
        Glide
                .with(context)
                .load(url)
                .asBitmap()
                .thumbnail(0.5f)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.e("Ready","Ready");
                        LatLng sydney = new LatLng(-34, 151);
                        mMap.addMarker(new MarkerOptions().position(coord).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(AlbumOnMapActivity.this,marker))));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord));
                        return false;
                    }
                })
                .into(markerImage);
    }
}

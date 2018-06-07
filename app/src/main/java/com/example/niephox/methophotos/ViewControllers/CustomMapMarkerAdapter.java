package com.example.niephox.methophotos.ViewControllers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.example.niephox.methophotos.Entities.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomMapMarkerAdapter {
    private Context context;
    private InfoWindowData imageData;
    private GoogleMap mMap;
    private View marker;
    private LatLng coordinates;

    public CustomMapMarkerAdapter(Context context, InfoWindowData imageData, GoogleMap mMap, View marker, LatLng coordinates) {
        this.context = context;
        this.imageData = imageData;
        this.mMap = mMap;
        this.marker = marker;
        this.coordinates = coordinates;
    }
    private CustomMapMarkerAdapter(Builder builder){
        this.context = builder.context;
        this.imageData = builder.imageData;
        this.mMap = builder.mMap;
        this.marker = builder.marker;
        this.coordinates = builder.coordinates;
    }

    public void setCustomInfoWindow(){
        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(context);
        mMap.setInfoWindowAdapter(customInfoWindow);
    }

    private Bitmap createBitmapFromView(Context context,View marker){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public void createCustomMarker(){
        Bitmap viewBitmap = createBitmapFromView(context,marker);
        Marker m =mMap.addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.fromBitmap(viewBitmap)));
        m.setTag(imageData);
    }
    public static class Builder{
        private Context context;
        private InfoWindowData imageData = new InfoWindowData("https://firebasestorage.googleapis.com/v0/b/methopro.appspot.com/o/EiuCEHFOOdNbOjxtVF73RIn0lOM2%2F26d25334-898e-4a56-899c-a8797964250f?alt=media&token=32c7abe1-c6d3-4c65-9a50-d77d5305ee32","DreamCity","DreamCountry","21/8/1997");
        private GoogleMap mMap;
        private View marker;
        private LatLng coordinates = new LatLng(41.093409,23.533739);
        public Builder context(final Context context){
            this.context=context;
            return this;
        }

        public Builder imageData(final InfoWindowData imageData) {
            this.imageData = imageData;
            return this;
        }

        public Builder map(final GoogleMap mMap) {
            this.mMap = mMap;
            return this;
        }

        public Builder marker(final View marker) {
            this.marker = marker;
            return this;
        }

        public Builder coordinates(final LatLng coordinates) {
            this.coordinates = coordinates;
            return this;
        }
        public Builder coordinates(final double latittude,final double longitude) {
            this.coordinates = new LatLng(latittude,longitude);
            return this;
        }
        public CustomMapMarkerAdapter build(){
            return new CustomMapMarkerAdapter(this);
        }
    }
}

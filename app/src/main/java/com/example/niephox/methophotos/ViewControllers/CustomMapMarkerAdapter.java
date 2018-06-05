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
}

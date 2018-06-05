package com.example.niephox.methophotos.Controllers;

import android.graphics.Bitmap;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.niephox.methophotos.ViewControllers.CustomMapMarkerAdapter;


public class CustomMapRequestListener implements RequestListener<String,Bitmap>{
    private CustomMapMarkerAdapter mapMarkerAdapter;

    public CustomMapRequestListener(CustomMapMarkerAdapter mapMarkerAdapter) {
        this.mapMarkerAdapter = mapMarkerAdapter;
    }

    @Override
    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
        mapMarkerAdapter.setCustomInfoWindow();
        mapMarkerAdapter.createCustomMarker();

        return false;
    }
}

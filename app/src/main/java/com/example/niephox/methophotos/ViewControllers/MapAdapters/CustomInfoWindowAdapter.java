package com.example.niephox.methophotos.ViewControllers.MapAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.niephox.methophotos.Entities.InfoWindowData;
import com.example.niephox.methophotos.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        final View view = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_googlemap_info_window, null);
        TextView cityTextView = view.findViewById(R.id.cityTextView);
        TextView countryTextView = view.findViewById(R.id.countryTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        ImageView image = view.findViewById(R.id.infoWindowImageView);

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        cityTextView.setText(infoWindowData.getImageCityLocation());
        countryTextView.setText(infoWindowData.getImageCountryLocation());
        dateTextView.setText(infoWindowData.getImageDate());

        Glide
                .with(context)
                .load(infoWindowData.getImagePath())
                .asBitmap()
                .thumbnail(0.1f)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(image);

        return view;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        return null;
    }

}

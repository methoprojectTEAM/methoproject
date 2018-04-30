package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Date;
/**
 * Created by IgorSpiridonov
 */


public class PhotosGridViewAdapter extends ArrayAdapter<Image> {

    Context context;
    ViewHolder viewHolder = new ViewHolder();
    ArrayList<Image> alImages = new ArrayList<>();

    public PhotosGridViewAdapter(Context context, ArrayList<Image> alImages) {
        super(context, R.layout.gridview_relative_layout, alImages);
        this.alImages = alImages;
        this.context = context;
    }
    @Override
    public int getCount() {
        Log.e("NUMBER OF PHOTOS IS", alImages.size() + "");
        return alImages.size();
    }
    @Nullable
    @Override
    public Image getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder=new ViewHolder();
        if (convertView == null) {
            final LayoutInflater layoutInflater =LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.photo_view_relative_layout,null);
        }

        viewHolder.ivImage =(ImageView)convertView.findViewById(R.id.photoViewRelative);

        Glide.with(context)
                .load(alImages.get(position).downloadUrl)
                .thumbnail(0.01f)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(viewHolder.ivImage);

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivImage;
    }
}

package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Date;

public class AlbumsGridViewAdapter extends ArrayAdapter<Album> {


    Context context;
    ViewHolder viewHolder;
    ArrayList<Album> alAlbums = new ArrayList<>();

    public AlbumsGridViewAdapter(Context context, ArrayList<Album> alAlbums) {
        super(context, R.layout.gridview_relative_layout, alAlbums);
        this.alAlbums = alAlbums;
        this.context = context;
    }

    @Override
    public int getCount() {
        Log.e("NUMBER OF ALBUMS IS", alAlbums.size() + "");
        return alAlbums.size();
    }

    @Nullable
    @Override
    public Album getItem(int position) {
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
    public View getView(int position,View convertView,ViewGroup parent) {
        viewHolder=new ViewHolder();
        if (convertView == null) {
            final LayoutInflater layoutInflater =LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.gridview_relative_layout,null);
        }

        viewHolder.ivImage =(ImageView)convertView.findViewById(R.id.imageViewRelative);
        viewHolder.nameTextView =(TextView) convertView.findViewById(R.id.textView2);
        viewHolder.creationDateTextView = (TextView) convertView.findViewById((R.id.textView));

        viewHolder.nameTextView.setText(alAlbums.get(position).name);
        Date date = new Date();
        date = alAlbums.get(position).date;
        viewHolder.creationDateTextView.setText(date.getDate() + "/" +date.getMonth()+"/" +date.getYear());

        Glide.with(context)
                .load(alAlbums.get(position).images.get(0).downloadUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.ivImage);

        return convertView;
    }
    private static class ViewHolder {
        ImageView ivImage;
        TextView nameTextView;
        TextView creationDateTextView;
    }
}

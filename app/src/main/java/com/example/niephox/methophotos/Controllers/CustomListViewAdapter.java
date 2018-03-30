package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;

/**
 * Created by Niephox on 3/30/2018.
 */

public class CustomListViewAdapter extends ArrayAdapter<Image> implements View.OnClickListener {
    private ArrayList<Image> imagesSet;
    Context mContext;



    // View lookup cache
    private static class ViewHolder {
        TextView tvDescription;
        ImageView imageView;
    }


   public CustomListViewAdapter( ArrayList<Image> images, Context context){
        super(context, R.layout.mainlistview_item,images);
        this.imagesSet = images;
        this.mContext = context;
   }


    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       Image image = getItem(position);
       ViewHolder viewHolder;
       final View result;

       if(convertView == null){
           viewHolder = new ViewHolder();
           LayoutInflater inflater = LayoutInflater.from(getContext());
           convertView = inflater.inflate(R.layout.mainlistview_item, parent,false);
           viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
           viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.description);

           result = convertView;
           convertView.setTag(viewHolder);
       }else
       {
           viewHolder = (ViewHolder) convertView.getTag();
           result = convertView;
       }
        viewHolder.tvDescription.setText(image.getDescription());
        Glide.with(mContext).load(image.getDownloadUrl()).into(viewHolder.imageView);
        return convertView;
    }
}
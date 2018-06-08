package com.example.niephox.methophotos.ViewControllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niephox.methophotos.Activities.AlbumOnMapActivity;
import com.example.niephox.methophotos.Activities.AlbumsViewActivity;
import com.example.niephox.methophotos.Controllers.FirebaseService;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Date;
/**
 * Created by IgorSpiridonov
 */


public class PhotosGridViewAdapter extends ArrayAdapter<Image> {

    private Context context;
    private ViewHolder viewHolder = new ViewHolder();
    private ArrayList<Image> alImages = new ArrayList<>();
    private String albumName;
    private FirebaseService service;
    public PhotosGridViewAdapter(Context context, ArrayList<Image> alImages) {
        super(context, R.layout.gridview_relative_layout, alImages);
        this.alImages = alImages;
        this.context = context;
        service = new FirebaseService(context);

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

    public void showPopupMenu(View view,int position, String albumName) {
        // inflate menu
        this.albumName = albumName;
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.image_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(position));
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;
        public MenuItemClickListener(int position) {
            this.position=position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.ShowMDItem:
                    //TODO: UPLOAD AlBUM
                    Toast.makeText(context, "UploadAlbum", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.MoveToAlbumItem:
                    service.deleteImageFromAlbum(alImages.get(position), albumName);
                    service.addImageToAlbum(alImages.get(position), "nealbum");
                    alImages.remove(position);
                    break;
                case R.id.DeleteItem:
                    service.deleteImageFromAlbum(alImages.get(position), albumName );
                    alImages.remove(position);

                    break;
                case R.id.EditComments:

                    break;
                default:

            }
            //TODO: IF ALIMAGES IS EMPTY MOVE OUT THE PHOTOVIEW ACTIVITY BACK TO THE ALBUMS VIEW
            notifyDataSetChanged();
            return true;
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder=new ViewHolder();
        if (convertView == null) {
            final LayoutInflater layoutInflater =LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.photo_view_relative_layout,null);
        }

        viewHolder.ivImage =(ImageView)convertView.findViewById(R.id.photoViewRelative);

        if(alImages.get(position).getDownloadUrl()==null){
            Glide.with(context)
                    .load(alImages.get(position).getImageURI())
                    .thumbnail(0.01f)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(viewHolder.ivImage);
        }
        else{
            Glide.with(context)
                    .load(alImages.get(position).getDownloadUrl())
                    .thumbnail(0.01f)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(viewHolder.ivImage);
        }
//        viewHolder.ivImage.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                showPopupMenu(view, position);
//                return false;
//            }
//        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivImage;
    }
}

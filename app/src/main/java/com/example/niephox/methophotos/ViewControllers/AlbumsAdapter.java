package com.example.niephox.methophotos.ViewControllers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.niephox.methophotos.Activities.PhotosViewActivity;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;
        public ImageView thumbnail, overflow;
        public Button btOpen;

        public MyViewHolder(View view) {
            super(view);
            btOpen = (Button) view.findViewById(R.id.btOpen);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.description.setText(album.getDescription());
        holder.btOpen.setOnClickListener(new ButtonClickListener(position,albumList.get(position)));
        Image albumThumbnail = new Image();
            albumThumbnail=album.getThumbnail();
        if (albumThumbnail.getImageURI() == null) {
            Glide.with(mContext).load(albumThumbnail.getDownloadUrl()).into(holder.thumbnail);
        } else {
             Glide.with(mContext).load(albumThumbnail.getImageURI()).into(holder.thumbnail);
       }

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.album_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.uploadAlbum:
                    //TODO: UPLOAD AlBUM
                    Toast.makeText(mContext, "UploadAlbum", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deleteAlbum:
                    //TODO: DELETE ALBUM
                    Toast.makeText(mContext, "DeleteAlbum", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    class ButtonClickListener implements View.OnClickListener {
        private ArrayList<Image> alImages = new ArrayList<>();
        private int position;
        private Album album;

        ButtonClickListener(int position, Album album) {
            this.position = position;
            this.album = album;
        }

        @Override
        public void onClick(View view) {
            alImages.clear();
            alImages.addAll(album.getImages());
            Intent intent = new Intent(mContext, PhotosViewActivity.class);

            intent.putExtra("alImages", alImages);
            mContext.startActivity(intent);

        }
    }

}
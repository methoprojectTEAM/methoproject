package com.example.niephox.methophotos.ViewControllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.example.niephox.methophotos.Activities.AlbumOnMapActivity;
import com.example.niephox.methophotos.Activities.PhotosViewActivity;
import com.example.niephox.methophotos.Controllers.AlbumBuilder;
import com.example.niephox.methophotos.Controllers.FirebaseService;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder>    {

    private Context mContext;
    private List<Album> albumList;
    private FirebaseService fbService = new FirebaseService();
    private AlbumBuilder AAGbuilder;
    private static iAsyncCallback iAsyncCallback;

    public AlbumBuilder getAAGbuilder(){return this.AAGbuilder;}
    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
    }

    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;

    }
    public void AAGinit(AlbumBuilder.AAG_BASE base,Album AAGalbum ){
        this.AAGbuilder = new AlbumBuilder(((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content), mContext, base);
        iAsyncCallback.RetrieveData(com.example.niephox.methophotos.Interfaces.iAsyncCallback.REQUEST_CODE.AUTOGENERATE);
        this.AAGbuilder.execute(AAGalbum.getImages());

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.description.setText(album.getDescription());
        holder.date.setText(album.getDate().toString());
        holder.btOpen.setOnClickListener(new ButtonClickListener(position, albumList.get(position)));
        Image albumThumbnail = new Image();
        albumThumbnail = album.getThumbnail();
        if (albumThumbnail.getDownloadUrl() == null) {
            Glide.with(mContext).load(albumThumbnail.getImageURI()).thumbnail(0.5f).into(holder.thumbnail);
        } else {
            Glide.with(mContext).load(albumThumbnail.getDownloadUrl()).thumbnail(0.5f).into(holder.thumbnail);
        }

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopupMenu(holder.overflow, position);

            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.album_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener(position));
        if (position != 0) {
            popup.getMenu().removeItem(R.id.AlbumAAG);
        }
        popup.show();
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    private void generateAlbums(final Album AAGalbum ) {
        String[] AAGoptions = mContext.getResources().getStringArray(R.array.AAGoptions);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle("Select Reader");
        mBuilder.setSingleChoiceItems(AAGoptions, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //Location
                        AAGinit(AlbumBuilder.AAG_BASE.LOCATION,AAGalbum);
                        break;
                    case 1:
                        //Date
                        AAGinit(AlbumBuilder.AAG_BASE.DATE,AAGalbum);
                        break;
                    default:

                        break;
                }

                dialog.dismiss();
            }
        });
        //
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, date;
        public ImageView thumbnail, overflow;
        public Button btOpen;

        public MyViewHolder(View view) {
            super(view);
            btOpen = (Button) view.findViewById(R.id.btOpen);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            description = (TextView) view.findViewById(R.id.description);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    /**
     * Click listener for popup menu items
     */
    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        public MenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.uploadAlbum:
                    //TODO: UPLOAD AlBUM
                    Toast.makeText(mContext, "UploadAlbum", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deleteAlbum:
                    fbService.queryAlbumDelete(albumList.get(position).getName());
                    Toast.makeText(mContext, "DeleteAlbum", Toast.LENGTH_SHORT).show();
                    albumList.remove(position);
                    notifyDataSetChanged(); //this is the only place that we need to run the notification
                    return true;
                case R.id.mapAlbum:
                    Intent intent = new Intent(mContext, AlbumOnMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("alImages", albumList.get(position).getImages());
                    intent.putExtras(bundle);
                    //intent.putExtra("alImages", albumList.get(position).getImages());
                    mContext.startActivity(intent);
                    return true;
                case R.id.AlbumAAG:
                    generateAlbums(albumList.get(position));
                    break;
                default:
            }

            return false;
        }
    }

    class ButtonClickListener implements View.OnClickListener, iAsyncCallback {
        Intent intent;
        ArrayList<String> albumsNew = new ArrayList<>();
        String[] albumListNames;
        FirebaseService firebaseService = new FirebaseService();
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
            intent = new Intent(mContext, PhotosViewActivity.class);
            intent.putExtra("alImages", alImages);
            intent.putExtra("albumName", album.getName());
            firebaseService.RegisterCallback(this);
            firebaseService.getAlbumsExceptLocalAndChosen(album.getName());
        }


        @Override
        public void RefreshView(REQUEST_CODE rq) {

        }

        @Override
        public void RetrieveData(REQUEST_CODE rq) {
            albumsNew = firebaseService.getAlbumsExceptLocalAndChosen();
            albumListNames = new String[albumsNew.size()];
            for (int i = 0; i < albumsNew.size(); i++)
                albumListNames[i] = (albumsNew.get(i));
            intent.putExtra("userAlbumsNames", albumListNames);
            mContext.startActivity(intent);
        }
    }

}
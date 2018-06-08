package com.example.niephox.methophotos.ViewControllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niephox.methophotos.Controllers.AlbumRepository;
import com.example.niephox.methophotos.Controllers.FirebaseService;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;

/**
 * Created by IgorSpiridonov
 */


public class PhotosGridViewAdapter extends ArrayAdapter<Image> {

    private Context context;
    private ViewHolder viewHolder = new ViewHolder();
    private ArrayList<Image> alImages = new ArrayList<>();
    private String[] userAlbumsNames;
    private String albumName;
    private String comments;

    private AlertDialog editCommentsDialog;
    private EditText commentsEditText;
    private View editCommentsView;
    private Button saveCommentsButton;

    private FirebaseService service;

    public PhotosGridViewAdapter(Context context, ArrayList<Image> alImages, String[] userAlbumsNames,AlertDialog editCommentsDialog,EditText commentsEditText,View editCommentsView) {
        super(context, R.layout.gridview_relative_layout, alImages);
        this.alImages = alImages;
        this.context = context;
        this.userAlbumsNames = userAlbumsNames;
        service = new FirebaseService(context);
        this.editCommentsDialog=editCommentsDialog;
        this.commentsEditText=commentsEditText;
        this.editCommentsView=editCommentsView;
        saveCommentsButton = editCommentsView.findViewById(R.id.saveCommentsButton);





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
                    final MoveDialogView dialog = new MoveDialogView(position, alImages.get(position), albumName, userAlbumsNames, context);
                    dialog.showDialogView();
                    break;
                case R.id.DeleteItem:
                    service.deleteImageFromAlbum(alImages.get(position), albumName );
                    alImages.remove(position);
                    notifyDataSetChanged();
                    break;
                case R.id.EditComments:
                    editCommentsDialog.show();

                    saveCommentsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            comments=commentsEditText.getText().toString();
                            alImages.get(position).setDescription(comments);
                            editCommentsDialog.hide();
                            service.setImageComments(albumName,comments,position);
                        }
                    });

                    //TODO:ADD FIREBASE SUPPORt


                    break;
                default:

            }
            //TODO: IF ALIMAGES IS EMPTY MOVE OUT THE PHOTOVIEW ACTIVITY BACK TO THE ALBUMS VIEW
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

        return convertView;
    }

    private class MoveDialogView {
        private  AlertDialog.Builder selectionDialogBuilder;
        private  AlertDialog selectAlbumDialog;
        private Image imageToTransfer;
        private String[] userAlbums;
        private String currentAlbum;
        private Context context;
        private int currentImagePosition;
        int position;

        public MoveDialogView(int currentImagePosition, Image imageToTransfer, String currentAlbum, String[] userAlbums,  Context context) {
            this.currentImagePosition = currentImagePosition;
            this.imageToTransfer = imageToTransfer;
            this.userAlbums = userAlbums;
            this.currentAlbum = currentAlbum;
            this.context = context;
        }

        public void showDialogView() {
            selectionDialogBuilder = new AlertDialog.Builder(context);
            selectionDialogBuilder.setTitle("Albums");
            selectionDialogBuilder.setSingleChoiceItems(userAlbums, -1, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    position = i;
                }
            });

            selectionDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectAlbumDialog.dismiss();
                }
            });
            selectionDialogBuilder.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AlbumRepository.transferImage(imageToTransfer, currentAlbum ,userAlbums[position]  , context);
                    alImages.remove(currentImagePosition);
                    notifyDataSetChanged();
                    selectAlbumDialog.dismiss();
                }
            });
            selectAlbumDialog = selectionDialogBuilder.create();
            selectAlbumDialog.show();

        }
    }
    private static class ViewHolder {
        ImageView ivImage;
    }
}

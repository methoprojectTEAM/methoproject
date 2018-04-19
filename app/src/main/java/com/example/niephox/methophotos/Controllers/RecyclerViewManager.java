package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.niephox.methophotos.Activities.MetadataActivity;
import com.example.niephox.methophotos.Activities.PhotoViewActivity;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;

/**
 * Created by Niephox on 4/15/2018.
 */


public class RecyclerViewManager  extends RecyclerView.Adapter<RecyclerViewManager.ViewHolder> implements View.OnClickListener , View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
    private ArrayList<Image> imageDataset;
    private  Context context;


    @Override
    public void onClick(View v) {
        Log.w("clicked","CLICKED");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        PopupMenu popup = new PopupMenu(v.getContext(),v);
        popup.getMenuInflater().inflate(R.menu.image_menu,popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ViewItem:
                Intent ViewItemIntent = new Intent(context.getApplicationContext(),PhotoViewActivity.class);
                context.getApplicationContext().startActivity(ViewItemIntent);
                Log.w("MenuItemclicked", "CLICKED  view item"  );
                break;
            case R.id.ShowMDItem:
                Intent ShowMetadataIntent = new Intent(context.getApplicationContext(),MetadataActivity.class);
                context.getApplicationContext().startActivity(ShowMetadataIntent);
                Log.w("MenuItemclicked", "CLICKED  show metadata " );
                break;
            case R.id.AddMDItem:
                //Intent AddMDtent = new Intent(context.getApplicationContext(),PhotoViewActivity.class);
                //Context.getApplicationContext().startActivity(ViewItemIntent);
                Log.w("MenuItemclicked", "CLICKED  addItem ");
                break;
            case R.id.MoveToAlbumItem:
                //TODO
                Log.w("MenuItemclicked", "CLICKED  Album" );
                break;
            case R.id.DeleteItem:
                //TODO
                Log.w("MenuItemclicked", "CLICKED  Delete" );

                break;
            default:
                break;
        }
        return false;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
              mTextView = view.findViewById(R.id.description);
              imageView = view.findViewById(R.id.image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewManager(ArrayList<Image> imageDataset, Context context) {
        this.imageDataset = imageDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewManager.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainlistview_item, parent, false);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);


        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(imageDataset.get(position).getDescription());
        if (imageDataset.get(position).getPath()== null) {
            Glide.with(context).load(imageDataset.get(position).getDownloadUrl()).into(holder.imageView);
        }else{
            Glide.with(context).load(imageDataset.get(position).getPath()).into(holder.imageView);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return imageDataset.size();
    }
}

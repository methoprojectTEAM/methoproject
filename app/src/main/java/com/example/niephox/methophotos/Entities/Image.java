package com.example.niephox.methophotos.Entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.drew.metadata.Metadata;

import java.util.UUID;
import java.util.ArrayList;



/**
 * Created by Niephox on 3/30/2018.
 */
public class Image implements Parcelable {
    private String storageLocationURL;
    private String downloadUrl;
    private String imageURI;
    private String name;
    private Album album;
    private ArrayList<String> metadata=new ArrayList<>();
    private String description;
    private ArrayList<String> imagesPath;
    public Image() {
        setName();
    }

    public ArrayList<String> getImagesPath() {
        return imagesPath;
    }

    public Image(String storageLocationURL, String downloadUrl, String name, Album album, ArrayList<String> metadata, String description) {
        this.storageLocationURL = storageLocationURL;
        this.downloadUrl = downloadUrl;
        this.name = name;
        this.album = album;
        this.metadata = metadata;
        this.description = description;
    }
    public Image(String imageURI){
        this.imageURI = imageURI;
        setName();
    }

    public Image(String storageLocationURL, String downloadUrl, String description) {
        this.storageLocationURL = storageLocationURL;
        this.downloadUrl = downloadUrl;
        this.description = description;
    }

    public String  getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getStorageLocationURL() {
        return storageLocationURL;
    }

    public void setStorageLocationURL(String storageLocationURL) {
        this.storageLocationURL = storageLocationURL;
    }

    public String getName() {
        return name;
    }

    public void setName() {
        UUID uuid;
        if(this.name == null) {
            uuid = UUID.randomUUID();
            this.name = uuid.toString();
        }
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public ArrayList<String> getMetadata() {
        return metadata;
    }



    public void setMetadata(ArrayList<String> metadata) {
        this.metadata.clear();
        this.metadata.addAll(metadata);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    private Image(Parcel in) {
        this.storageLocationURL=in.readString();
        this.downloadUrl=in.readString();
        this.description=in.readString();
        this.imageURI=in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.storageLocationURL);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.description);
        dest.writeString(this.imageURI);
    }
}
package com.example.niephox.methophotos.Entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.drew.metadata.Metadata;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Niephox on 3/30/2018.
 * Modified by IgorSpiridonov
 * {
 *     Implemented Parcelable
 * }
 */

public class Image implements Parcelable {
    public String storageLocationURL;
    public String downloadUrl;
    public String imageURI;
    public String name;
    public Album album;
    public Metadata metadata;
    public String description;

    //String str_folder;
    ArrayList<String> al_imagepath;

    /*public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }*/

    public ArrayList<String> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }
    public Image(String storageLocationURL, String downloadUrl, String name, Album album, Metadata metadata, String description) {
        this.storageLocationURL = storageLocationURL;
        this.downloadUrl = downloadUrl;
        this.name = name;
        this.album = album;
        this.metadata = metadata;
        this.description = description;
    }
    public Image(String imageURI){
        this.imageURI = imageURI;
    }

    public Image (){}

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

    public void setName(String name) {
        this.name = name;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Metadata getMetadata() {
        return metadata;
    }



    public void setMetadata(Metadata metadata) {

        this.metadata = metadata;
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

    }
}

package com.example.niephox.methophotos.Entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.drew.lang.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by Niephox on 3/30/2018.
 */
public class Image implements Parcelable {
    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    public HashMap<String, Object> infoMap;
    private String storageLocationURL;
    private String downloadUrl;
    private String imageURI;
    private String name;
    private Album album;
    private ArrayList<String> metadata = new ArrayList<>();
    private String description="Your Comments";
    private ArrayList<String> imagesPath;
    private LatLng parcableLocation = null;


    public Image() {
        setName();
    }

    public Image(String storageLocationURL, String downloadUrl, String name, Album album, ArrayList<String> metadata, String description) {
        this.storageLocationURL = storageLocationURL;
        this.downloadUrl = downloadUrl;
        this.name = name;
        this.album = album;
        this.metadata = metadata;
        this.description = description;
    }

    public Image(String imageURI) {
        this.imageURI = imageURI;
        setName();
    }

    public Image(String storageLocationURL, String downloadUrl, String description) {
        this.storageLocationURL = storageLocationURL;
        this.downloadUrl = downloadUrl;
        this.description = description;
    }

    private Image(Parcel in) {
        this.storageLocationURL = in.readString();
        this.downloadUrl = in.readString();
        this.description = in.readString();
        this.imageURI = in.readString();
        this.parcableLocation= in.readParcelable(LatLng.class.getClassLoader());
    }

    public LatLng getParcableLocation() {
        return parcableLocation;
    }

    public ArrayList<String> getImagesPath() {
        return imagesPath;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    private void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setUrls(Uri downloadUrl, String storageLocationURL) {
        String downloadString = downloadUrl == null ? "" : downloadUrl.toString();
        setDownloadUrl(downloadString);
        setStorageLocationURL(storageLocationURL);
    }

    public String getStorageLocationURL() {
        return storageLocationURL;
    }

    private void setStorageLocationURL(String storageLocationURL) {
        this.storageLocationURL = storageLocationURL;
    }

    public String getName() {
        return name;
    }

    public void setName() {
        UUID uuid;
        if (this.name == null) {
            uuid = UUID.randomUUID();
            this.name = uuid.toString();
        }
    }

    public HashMap<String, Object> getInfoMap() {
        return infoMap;
    }

    public void setInfoMap(HashMap<String, Object> infoMap) {
        this.infoMap = infoMap;
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
        if(this.infoMap != null) {
            if (this.infoMap.get("Location") != null) {
                GeoLocation location = (GeoLocation) this.infoMap.get("Location");
                this.parcableLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        dest.writeParcelable(this.parcableLocation, flags);
    }


}
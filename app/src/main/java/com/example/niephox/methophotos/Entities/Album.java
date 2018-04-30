package com.example.niephox.methophotos.Entities;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Niephox on 3/30/2018.
 */

public class Album {
    public String name;
    public Date date;
    public String description;
    public ArrayList<Image> images;

    public Album() {
    }

    public Album(String name, Date date, String description, ArrayList<Image> images) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.images = images;
    }

    public Album(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }
}



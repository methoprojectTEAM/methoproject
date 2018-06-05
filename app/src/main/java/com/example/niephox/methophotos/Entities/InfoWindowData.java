package com.example.niephox.methophotos.Entities;

public class InfoWindowData {
    private String imagePath;
    private String imageCityLocation;
    private String imageCountryLocation;
    private String imageDate;

    public InfoWindowData(String imagePath, String imageCityLocation, String imageCountryLocation, String imageDate) {
        this.imagePath = imagePath;
        this.imageCityLocation = imageCityLocation;
        this.imageCountryLocation = imageCountryLocation;
        this.imageDate = imageDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImageCityLocation() {
        return imageCityLocation;
    }

    public String getImageDate() {
        return imageDate;
    }

    public String getImageCountryLocation() {
        return imageCountryLocation;
    }

    public void setImageCountryLocation(String imageCountryLocation) {
        this.imageCountryLocation = imageCountryLocation;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImageCityLocation(String imageCityLocation) {
        this.imageCityLocation = imageCityLocation;
    }

    public void setImageDate(String imageDate) {
        this.imageDate = imageDate;
    }
}

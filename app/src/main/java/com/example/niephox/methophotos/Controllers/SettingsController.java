package com.example.niephox.methophotos.Controllers;

/**
 * Created by Niephox on 4/19/2018.
 */

public class SettingsController {
    /**
     * Changing the settings of the app based on user preferences
     * to let certain  app functionalites to work automaticly or not.
     **/
    public static Boolean AutoSignIn = false;
    public static Boolean AutoCreateAlbum = true;
    public static Boolean RecieveGalleryPhotos = true;

    public static void setAutoCreateAlbum(Boolean autoCreateAlbum) {
        AutoCreateAlbum = autoCreateAlbum;
    }

    public static void setRecieveGalleryPhotos(Boolean recieveGalleryPhotos) {
        RecieveGalleryPhotos = recieveGalleryPhotos;
    }

    public static void setAutoSignIn(Boolean autoSignIn) {
        AutoSignIn = autoSignIn;
    }

}

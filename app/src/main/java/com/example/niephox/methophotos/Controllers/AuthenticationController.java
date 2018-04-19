package com.example.niephox.methophotos.Controllers;

import com.example.niephox.methophotos.Entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by Niephox on 4/18/2018.
 */

public class AuthenticationController {
    private static FirebaseAuth AuthRef = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser = AuthRef.getCurrentUser();
    private static DatabaseController databaseController = new DatabaseController();

    public static void deleteUser ( ){
        databaseController.deleteUserDatabase(currentUser.getUid());
        currentUser.delete();
        //TODO:: Implement call
    }

    public static void UpdateEmail (String newEmail){
        currentUser.updateEmail(newEmail);
        databaseController.changeUserEmailDatabase(currentUser.getUid(), newEmail);
    }

    public static void UpdatePassword(String newPassword){
        currentUser.updatePassword(newPassword);
    }

    public static void SentEmailVerification(){
        currentUser.sendEmailVerification();
    }


}

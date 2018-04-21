package com.example.niephox.methophotos.Controllers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.niephox.methophotos.Entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    private static AuthCredential userCredential;
    private static String FIREBASE_AUTH;


    public static void deleteUser() {
        databaseController.deleteUserDatabase(currentUser.getUid());
        currentUser.delete();
        //TODO:: Implement call
    }

    public static void UpdateEmail(String newEmail) {
        currentUser.updateEmail(newEmail);
        databaseController.changeUserEmailDatabase(currentUser.getUid(), newEmail);
    }

    public static void UpdatePassword(String newPassword) {
        currentUser.updatePassword(newPassword);
    }

    public static void SentEmailVerification() {
        currentUser.sendEmailVerification();
    }

    public static void ReAuthenticateUser(String Email, String Password) {
        userCredential = EmailAuthProvider.getCredential(Email, Password);
        currentUser.reauthenticate(userCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.w("FBAUTH", "USER REAUTHENTICATION COMPLETE");
                //TODO:: Implement on  complete reauthenitcation callback.
            }
        });
    }

    public static FirebaseUser GetCurrentlySignedUser() {
        return currentUser;
    }

    public static boolean isUserSignedIn() {
        if (currentUser != null) {
            Log.w(FIREBASE_AUTH, "User Signed IN");
            return true;
        } else {
            Log.w(FIREBASE_AUTH, "User is not signed in");
            return false;
        }
    }

    public static void UserSignOut() {
        AuthRef.signOut();
    }
}

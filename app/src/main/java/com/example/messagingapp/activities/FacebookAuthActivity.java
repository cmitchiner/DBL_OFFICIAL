package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.messagingapp.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FacebookAuthActivity extends MainActivity {

    /**
     * VARIABLES
     **/
    CallbackManager callbackManager;
    //Firebase authentication variable
    FirebaseAuth firebaseAuth;

    /**
     * onCreate() is the first method that runs when the activity is started
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //Create callback manager
        callbackManager = CallbackManager.Factory.create();

        //Opens a google chrome tab to facebook login page
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            /** LISTENERS FOR SUCCESSFUL LOGIN, CANCELED LOGIN, and ERROR */
            @Override
            public void onSuccess(LoginResult loginResult) {
                //if successful begin the OAUTH firebase process
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //Do nothing as it will redirect back to login page
            }

            @Override
            public void onError(FacebookException exception) {
                //Do nothing as it will redirect back to login page
            }
        });
    }

    /**
     * After login is successful, this function is called
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * When a user is signed out their notification token is deleted,
     * thus when they sign in we need to recreate the
     * notification token, this grabs a new token and calls the update token function with said token
     */
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    /**
     * Updates the token on the database
     *
     * @param token the token to be sent to the database
     */
    private void updateToken(String token) {
        //Reference the firestore database
        FirebaseFirestore fireBaseFacebook = FirebaseFirestore.getInstance();
        //Store token in hashmap to pass to DB
        Map<String, Object> data = new HashMap<>();
        data.put("Token", token);

        //Store hashamp in proper collection
        fireBaseFacebook.collection("Tokens").document(firebaseAuth.getCurrentUser().getUid()).set(data).addOnSuccessListener(
                unused -> Log.d("TOKEN", "UPDATED")).addOnFailureListener(e -> Log.d("TOKEN", "Error Updating: " + e));
    }

    /**
     * Completes the firebase side of the login after facebook auth was successful
     *
     * @param token token from facebook side of login
     */
    private void handleFacebookAccessToken(AccessToken token) {

        //Create firebase auth credential from facebook auth
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //LOGIN SUCCESS
                if (task.isSuccessful()) {
                    // Update UI with the signed-in user's information
                    getToken();
                    MainActivity.isGuest = false;
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    //Store user in the database
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        addAcctToDB(user.getDisplayName(), user.getUid(), "", "Facebook Account");
                    } else {
                        Toast.makeText(FacebookAuthActivity.this, "Existing User found, signing in...", Toast.LENGTH_SHORT).show();
                        //Redirect to profile activity
                        startActivity(new Intent(FacebookAuthActivity.this, ProfileActivity.class));
                    }
                } else { //LOGIN FAIL
                    // If sign in fails, display a message to the user.
                    Toast.makeText(FacebookAuthActivity.this, "" + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * If an account is created via google sign in, we still need a reference to them in the DB, thus
     * this method does such.
     */
    private void addAcctToDB(String fullName, String username, String phone, String email) {

        User user = new User(fullName, username, phone, email);
        FirebaseDatabase.getInstance("https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                /** User posted to database successfully **/
                if (task.isSuccessful()) {
                    Toast.makeText(FacebookAuthActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(FacebookAuthActivity.this, ProfileActivity.class));
                } else {
                    /** User failed to be added to database **/
                    Toast.makeText(FacebookAuthActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
package com.example.messagingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    //Variables for references to activity_settings.xml
    private TextView fullNameTv, emailTv;
    private Button logoutBtn, changePassBtn;

    //Variable for Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    /** onCreate() is a method that runs before a user see's the current activity
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Init references to activity_settings.xml
        fullNameTv = findViewById(R.id.fullNameTv);
        emailTv = findViewById(R.id.emailTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        changePassBtn = findViewById(R.id.changePassBtn);

        //Init On-Click Listeners
        logoutBtn.setOnClickListener(this);
        changePassBtn.setOnClickListener(this);

        //Init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();


        //Set Text-Fields to User name and email
        if (!MainActivity.isGuest) {
            firebaseUser = firebaseAuth.getCurrentUser();
            fullNameTv.setText(firebaseUser.getDisplayName());
            emailTv.setText(firebaseUser.getEmail());
        } else {
            fullNameTv.setText("GUEST");
            emailTv.setText("GUEST");
        }


    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logoutBtn:
                logoutUser();
                break;
            case R.id.changePassBtn:
                if (!MainActivity.isGuest) {
                    startActivity(new Intent(this, ChangePasswordActivity.class));
                } else {
                    Toast.makeText(SettingsActivity.this, "This feature is not allowed for guests", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * logoutUser(): Logs a user out and redirects them to the Login Screen
     *
     * @pre @code{FirebaseAuth.getCurrentUser() != null}
     * @modifies FirebaseUser
     * @post @code{FirebaseAuth.getCurrentUser() == null}
     */
    private void logoutUser() {
        if (!MainActivity.isGuest) {
            firebaseAuth.signOut();
        }
        Toast.makeText(SettingsActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

}
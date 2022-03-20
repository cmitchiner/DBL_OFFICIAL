package com.example.messagingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        firebaseUser = firebaseAuth.getCurrentUser();

        //Set Text-Fields to User name and email
        fullNameTv.setText(firebaseUser.getDisplayName().toString());
        emailTv.setText(firebaseUser.getEmail());


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logoutBtn:
                break;
            case R.id.changePassBtn:
                break;
        }
    }
}
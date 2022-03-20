package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    /** VARIABLES **/
    //Firebase auth vars
    private FirebaseAuth firebaseAuth;

    //Var references to activity_forgot.xml file
    private EditText forgotEmailEt;
    private Button sendResetEmailBtn;

    /** onCreate() is a method that runs before a user see's the current activity
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        //Init references to activity_forgot_pass.xml
        forgotEmailEt = findViewById(R.id.forgotEmailEt);
        sendResetEmailBtn = findViewById(R.id.resetPassBtn);

        //Assign On-Click Listener for Button
        sendResetEmailBtn.setOnClickListener(this);

        //Init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * onClick(): holds all the on click listeners for any elements on the screen
     *
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetPassBtn:
                resetPassword();
                break;
        }
    }

    /**
     * Sends a reset password link to a user provided email
     *
     * @pre Email is associated with an account
     * @post Reset Password Email Sent
     */
    public void resetPassword() {

        //Pull email from EditText field
        String email = forgotEmailEt.getText().toString().trim();

        //Use Firebase to Send Reset Password Email
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            //Email was sent successfully
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassActivity.this, "Link has been sent to your email",
                        Toast.LENGTH_LONG).show();
                //Redirect to login page
                startActivity(new Intent(ForgotPassActivity.this, MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            //Email failed to send
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassActivity.this, "Reset link was not sent! "
                        + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
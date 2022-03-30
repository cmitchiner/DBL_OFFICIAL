package com.example.messagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messagingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private Button submitBtn;
    private TextView fullNameTv, emailTv;
    private EditText oldPassEt, newPassEt, confNewPassEt;
    private FirebaseAuth firebaseAuth;

    /** onCreate() is a method that runs before a user see's the current activity
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Init references to activity_change_password.xml
        fullNameTv = findViewById(R.id.fullNameTv);
        emailTv = findViewById(R.id.emailTv);
        submitBtn = findViewById(R.id.submitBtn);
        oldPassEt = findViewById(R.id.oldPassEt);
        newPassEt = findViewById(R.id.newPassEt);
        confNewPassEt = findViewById(R.id.confNewPassEt);

        //Init On-Click Listeners
        submitBtn.setOnClickListener(this);

        //Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Set TextViews for Name and Email
        fullNameTv.setText(firebaseAuth.getCurrentUser().getDisplayName());
        emailTv.setText(firebaseAuth.getCurrentUser().getEmail());

    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.submitBtn:
                changePassword();
                break;
        }
    }

    /**
     * Pulls data from text fields on screen, and calls helper functions to verify it is
     * in correct password format.
     *
     * @post if data is valid, begin password change process
     */
    private void changePassword() {
        String oldPass = oldPassEt.getText().toString().trim();
        String newPass = newPassEt.getText().toString().trim();
        String confNewPass = confNewPassEt.getText().toString().trim();

        //Verify all fields are not empty
        if (allFieldsValid(oldPass, newPass, confNewPass)) {
            //Attempt to change
            attemptChangePassRequest(oldPass, newPass);
        }
    }

    /**
     * Verifies all fields are non empty, password is greater than 6 characters, password is in correct
     * format, and confirmation password is equivalent to old password
     *
     * @param oldPass the users old password to be checked
     * @param newPass the new password to be checked
     * @param confNewPass the confirmation of the new password to be checked
     * @return true if all fields are valid and in correct format
     */
    private boolean allFieldsValid(String oldPass, String newPass, String confNewPass) {
        if (oldPass.isEmpty()) {
            oldPassEt.setError("You must provide your previous password!");
            oldPassEt.requestFocus();
            return false;
        }
        if (newPass.isEmpty()) {
            newPassEt.setError("You must provide a new password!");
            newPassEt.requestFocus();
            return false;
        }
        if (confNewPass.isEmpty()) {
            confNewPassEt.setError("This field is required!");
            confNewPassEt.requestFocus();
            return false;
        }
        if (!confNewPass.equals(newPass)) {
            confNewPassEt.setError("New Password fields do not match!");
            confNewPassEt.requestFocus();
        }
        if (newPass.length() < 6) {
            newPassEt.setError("Password must be at least 6 characters and at most 20");
            newPassEt.requestFocus();
            return false;
        }
        if (!passwordFollowsFormat(newPass)) {
            newPassEt.setError("Password must have at least one: capital letter, " +
                    "special character, and number");
            newPassEt.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Checks if a password is in an acceptable format:
     *      at least one special character, at least one capital, at least one number, and at least
     *      6 characters long.
     * @param password the password to be checked
     * @return result = true if password is valid, result = false if password is invalid
     */
    public static boolean passwordFollowsFormat(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{6,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * Authenticates the user with their old password, then updates password to given
     * param newPass.
     *
     * @param oldPass the old password to be used for reauthentication
     * @param newPass the new password the user wants
     */
    private void attemptChangePassRequest(String oldPass, String newPass) {
        //Get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //Create auth credential for authentication
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPass);
        firebaseUser.reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Old password was correct, and user has been authenticated, begin change process
                        firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Password updated successfully
                                    Toast.makeText(ChangePasswordActivity.this, "Password Updated!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                                } else {
                                    //Password failed to update
                                    Toast.makeText(ChangePasswordActivity.this, "Error Password not updated", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Failed to authenticate user with old password
                    Toast.makeText(ChangePasswordActivity.this, "Incorrect Old Password OR Using Google Account", Toast.LENGTH_LONG).show();
                }
        });
    }
}
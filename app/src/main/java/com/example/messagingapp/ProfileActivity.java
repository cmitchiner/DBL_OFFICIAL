package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, View.OnClickListener {

    /** VARIABLES **/
    //Variables for element references to activity_profile.xml
    private Button updateBtn;
    private TextView fullNameTv, emailTv;
    private EditText fullNameEt, usernameEt, phoneEt;
    private ImageView settingsBtn;
    private CardView cardAddListing;
    private BottomNavigationView bottomNavigationView;

    //Firebase variables
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    //Global Method variables
    private boolean usernameIsUnique;
    private String oldFullName, oldPhone;

    /** onCreate() is a method that runs before a user see's a page
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Init references to profile_activity.xml
        fullNameTv = findViewById(R.id.fullNameTv);
        emailTv = findViewById(R.id.emailTv);
        fullNameEt = findViewById(R.id.fullNameEt);
        usernameEt = findViewById(R.id.usernameEt);
        phoneEt = findViewById(R.id.phoneEt);
        updateBtn = findViewById(R.id.updateBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        cardAddListing = findViewById(R.id.cardAddListing);


        //Setup Navigation bar and highlight current page
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.profileNavBar);

        //Init On-Click Listeners
        settingsBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        cardAddListing.setOnClickListener(this);

        //Init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Set TextViews
        fullNameTv.setText(firebaseAuth.getCurrentUser().getDisplayName());
        emailTv.setText(firebaseAuth.getCurrentUser().getEmail());

        //Verify user is still logged in and update EditText fields
        verifyUserStatus();
    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.settingsBtn:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.cardAddListing:
                startActivity(new Intent(this, AddListingActivity.class));
                break;
            case R.id.updateBtn:
                updateUserInfo();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.profileNavBar:
                return true;
            case R.id.messages:
                startActivity(new Intent(this,MessagesActivity.class));
                return true;
        }
        return false;
    }

    private void verifyUserStatus() {
        FirebaseUser fireBaseUser = firebaseAuth.getCurrentUser();
        if (fireBaseUser != null) {
            //User is signed in
            pullUserInfo();

        } else {
            //No User found, redirect to login page
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void pullUserInfo() {
        FirebaseUser fireBaseUser = firebaseAuth.getCurrentUser();
        ref = FirebaseDatabase.getInstance(
                "https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                fullNameEt.setText(user.fullName);
                usernameEt.setText(user.username);
                phoneEt.setText(user.phone);
                oldFullName = user.fullName;
                oldPhone = user.phone;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PROFILE", "Failed to read user info from Database");
            }
        });
        fullNameTv.setText(firebaseAuth.getCurrentUser().getDisplayName());
        emailTv.setText(firebaseAuth.getCurrentUser().getEmail());
    }

    private void updateUserInfo() {
        if (verifyUpdatedFields()) {
            attemptUserInfoUpdate();
        }
    }

    private boolean verifyUpdatedFields() {
        String fullName = fullNameEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();

        if (fullName.isEmpty()) {
            fullNameEt.setError("Full name is required");
            fullNameEt.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            phoneEt.setError("Phone number is required");
            phoneEt.requestFocus();
            return false;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneEt.setError("Please provide a valid phone number");
            phoneEt.requestFocus();
            return false;
        }
        return true;
    }

    private void attemptUserInfoUpdate() {
        String newFullName = fullNameEt.getText().toString().trim();
        String newPhone = phoneEt.getText().toString().trim();

        ref = FirebaseDatabase.getInstance(
                "https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users").child(firebaseAuth.getCurrentUser().getUid());

        if (!oldFullName.equals(newFullName)) {
            ref.child("fullName").setValue(newFullName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this,
                                "Full Name updated successfully",
                                Toast.LENGTH_SHORT).show();
                        setFirebaseDisplayName(newFullName);
                        fullNameTv.setText(newFullName);
                    } else {
                        /** User info failed to be updated in database **/
                        Toast.makeText(ProfileActivity.this,
                                "Failed to update full name! Try again!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (!oldPhone.equals(newPhone)) {
            ref.child("phone").setValue(newPhone).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this,
                                "Phone number updated successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        /** User info failed to be updated in database **/
                        Toast.makeText(ProfileActivity.this,
                                "Failed to update phone number! Try again!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setFirebaseDisplayName(String fullName) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName).build();

        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("PROFILE_UPDATE", "Display Name Set Successfully!");
                        } else {
                            Log.d("PROFILE_UPDATE", "Failed to set DisplayName");
                        }
                    }
                });
    }

}
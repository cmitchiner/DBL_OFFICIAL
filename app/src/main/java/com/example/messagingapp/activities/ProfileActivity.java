package com.example.messagingapp.activities;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messagingapp.R;
import com.example.messagingapp.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    /**
     * VARIABLES
     **/
    //Variables for element references to activity_profile.xml
    private Button updateBtn;
    private TextView fullNameTv, emailTv;
    private EditText fullNameEt, usernameEt, phoneEt;
    private ImageView settingsBtn;
    private CardView cardAddListing;
    private BottomNavigationView bottomNavigationView;
    private RelativeLayout activeListings;
    private String passuser;

    //Firebase variables
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    //Global Method variables
    private boolean usernameIsUnique;
    private String currentFullName = null, currentPhone = null, currentUsername = null;

    /**
     * onCreate() is a method that runs before a user see's a page
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
        initXmlReferences();

        //Setup Navigation bar and highlight current page
        initNavigationBar();

        //Init On-Click Listeners
        initOnClickListeners();

        //Init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Init Firebase Database Reference
        if (!MainActivity.isGuest) {
            ref = FirebaseDatabase.getInstance(
                    "https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
        }

        //Verify user is still logged in and update EditText fields
        verifyLoginStatus();
    }

    /**
     * Initializes references to profile_activity.xml
     */
    private void initXmlReferences() {
        fullNameTv = findViewById(R.id.fullNameTv);
        emailTv = findViewById(R.id.emailTv);
        fullNameEt = findViewById(R.id.fullNameEt);
        usernameEt = findViewById(R.id.usernameEt);
        phoneEt = findViewById(R.id.phoneEt);
        updateBtn = findViewById(R.id.updateBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        cardAddListing = findViewById(R.id.cardAddListing);
        activeListings = findViewById(R.id.activeListingsOpen);
    }

    /**
     * Initializes bottom navigation bar
     */
    private void initNavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.profileNavBar);
    }

    /**
     * Initializes on click listeners
     */
    private void initOnClickListeners() {
        settingsBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        cardAddListing.setOnClickListener(this);
        activeListings.setOnClickListener(this);
    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settingsBtn:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.cardAddListing:
                if (!MainActivity.isGuest) {
                    //Start add listing process
                    startActivity(new Intent(this, AddListingActivity.class));
                } else {
                    Toast.makeText(ProfileActivity.this, "Guests cannot create listings!",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.updateBtn:
                if (!MainActivity.isGuest) {
                    //Start update profile process
                    updateUserInfo();
                } else {
                    Toast.makeText(ProfileActivity.this, "This feature is not allowed for guests"
                            , Toast.LENGTH_LONG).show();
                }
                break;
            //Change to !Guest
            case R.id.activeListingsOpen:
                if (!MainActivity.isGuest) {
                    //Show all of current users listings
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String usidCombo = passuser + ":" + id;
                    Log.d("filter", usidCombo);
                    Intent intent = new Intent(this, UserListingsActivity.class);
                    intent.putExtra("title", usidCombo);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Guests do not have active listings",
                            Toast.LENGTH_LONG).show();
                }

        }
    }


    /**
     * Verifies that the user is still logged in
     * If they are, pull latest user info from DB and fill out profile page accordingly
     * If they are not, redirect to login page
     */
    private void verifyLoginStatus() {
        FirebaseUser fireBaseUser = firebaseAuth.getCurrentUser();
        if (fireBaseUser != null || MainActivity.isGuest) {
            //User is signed in
            getCurrentUserInfo();
        } else {
            //No User found, redirect to login page
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Sets the text for all edit text and text views holding user profile information
     *
     * @param fullName name to be displayed on screen
     * @param username username to be displayed on screen
     * @param phone    phone to be displayed on screen
     */
    private void fillUserInfoFields(String fullName, String username, String phone) {
        FirebaseUser fireBaseUser = firebaseAuth.getCurrentUser();
        currentFullName = fullName;
        currentUsername = username;
        currentPhone = phone;
        fullNameTv.setText(fireBaseUser.getDisplayName());
        emailTv.setText(fireBaseUser.getEmail());
        fullNameEt.setText(currentFullName);
        usernameEt.setText(currentUsername);
        phoneEt.setText(currentPhone);
        if (currentUsername.equals(fireBaseUser.getUid().toString())) {
            usernameEt.setError("Please change your username!");
            usernameEt.requestFocus();
        }
    }

    /**
     * Grabs the latest user info from DB, and passes it to a function that will set the EditText
     * and TextViews accordingly.
     */
    private void getCurrentUserInfo() {
        if (MainActivity.isGuest) {
            //user is a guest so fill everything to be guest
            fullNameTv.setText("GUEST");
            emailTv.setText("GUEST");
            fullNameEt.setText("GUEST");
            usernameEt.setText("GUEST");
            phoneEt.setText("GUEST");
            fullNameEt.setEnabled(false);
            usernameEt.setEnabled(false);
            phoneEt.setEnabled(false);
        } else {
            //Pulling User Data
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Store user data in User object
                    User user = snapshot.getValue(User.class);
                    passuser = user.fullName;
                    fillUserInfoFields(user.fullName, user.username, user.phone);
                    String passuser = user.username;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("PROFILE", "Failed to read user info from Database");
                }
            });
        }
    }

    /**
     * Updates the user information if all fields are correctly filled and username is unique
     * Note: actual database update is done in function call attemptUserInfoUpdate()
     */
    private void updateUserInfo() {
        if (verifyUpdatedFields()) {
            String username = usernameEt.getText().toString().trim();
            /*The preceding code checks to see if any username in the database is equal to the
            username pulled from the EditText field.*/
            ref.getParent().orderByChild("username").equalTo(username).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //If snapshot exists it means we found a match, however we also need to
                            //verify the match is not our current username because it is possible
                            //the user never changed their username
                            if( usernameEt.length() < 4 ){
                                usernameEt.setError("Username is too short");
                            }
                            if( usernameEt.length() > 21 ){
                                usernameEt.setError("Username is too long");
                            }
                            if (snapshot.exists() && !username.equals(currentUsername)) {
                                usernameEt.setError("Username is already in use!");
                                usernameEt.requestFocus();
                            } else {
                                //No match found, thus proceed with update
                                attemptUserInfoUpdate();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProfileActivity.this,
                                    "We are experiencing server issues, please try again later",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
    }

    /**
     * Verifies all profile EditText fields are non-empty, and phone number is in a valid format
     *
     * @return true if no violations are found, false if any violations are found
     */
    private boolean verifyUpdatedFields() {
        String fullName = fullNameEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String username = usernameEt.getText().toString().trim();

        if (fullName.isEmpty()) {
            fullNameEt.setError("Full name is required");
            fullNameEt.requestFocus();
            return false;
        }
        if (username.isEmpty()) {
            usernameEt.setError("Username is required");
            usernameEt.requestFocus();
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

    /**
     * Checks all three EditText fields to see if the values changed, if they have changed it
     * then updates the database accordingly.
     */
    private void attemptUserInfoUpdate() {
        //Get the text that is currently in the EditText fields
        String newFullName = fullNameEt.getText().toString().trim();
        String newPhone = phoneEt.getText().toString().trim();
        String newUsername = usernameEt.getText().toString().trim();

        //Three if statements to verify if any or all EditText fields have changed
        //If they have changed attempt to update the value of the corresponding child
        if (!currentFullName.equals(newFullName)) {
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
        if (!currentUsername.equals(newUsername)) {
            ref.child("username").setValue(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this,
                                "Username updated successfully",
                                Toast.LENGTH_SHORT).show();
                        usernameEt.setError(null);

                    } else {
                        Toast.makeText(ProfileActivity.this,
                                "Failed to update username! Try again!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (!currentPhone.equals(newPhone)) {
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
        if (currentPhone.equals(newPhone) && currentFullName.equals(newFullName)
                && currentUsername.equals(newUsername)) {
            Toast.makeText(ProfileActivity.this, "Nothing to Update!",
                    Toast.LENGTH_SHORT).show();
        }
        //Refresh EditText fields, TextView at top of screen, and variables for current user info
        getCurrentUserInfo();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    /**
     * A users full name is stored both in a user object in the database and on their authentication
     * account, thus when we update the full name in the user object we must also update the full
     * name on the authentication account. This method does such.
     *
     * @param fullName a string representing the name to update the authentication account with
     * @post @code{FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == fullName}
     */
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Listing_Activity.class));
                return true;
            case R.id.profileNavBar:
                return true;
            case R.id.messages:
                if (MainActivity.isGuest) {
                    Toast.makeText(ProfileActivity.this, "This feature is not available for " +
                            "guests!", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    startActivity(new Intent(this, MessagesActivity.class));
                }
                return true;
        }
        return false;
    }

}
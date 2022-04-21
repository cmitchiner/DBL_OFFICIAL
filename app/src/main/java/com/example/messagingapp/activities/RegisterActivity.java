package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messagingapp.R;
import com.example.messagingapp.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * VARIABLES
     **/
    //Variables for references to activity_register.xml
    private Button registerBtn;
    private EditText registerFullNameEt, registerEmailEt, registerPasswordEt, registerUsernameEt;
    private EditText registerPhoneEt;

    //Variables for firebase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    //Global variable for method use
    private boolean usernameIsUnique;

    /**
     * onCreate() is a method that runs before a user see's the current activity
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Init references to activity_register.xml
        registerBtn = findViewById(R.id.btnRegister);
        registerFullNameEt = findViewById(R.id.inputFullName);
        registerEmailEt = findViewById(R.id.inputEmail);
        registerPasswordEt = findViewById(R.id.inputPassword);
        registerUsernameEt = findViewById(R.id.inputUsername);
        registerPhoneEt = findViewById(R.id.inputPhone);

        //Init On-Click Listeners
        registerBtn.setOnClickListener(this);

        //Init FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                registerUser();
                break;
        }
    }

    /**
     * Calls all helper functions to verify fields are filled correctly and username is unique, then calls
     * a function to begin firebase registration.
     */
    public void registerUser() {
        //Store all info from EditText fields
        String fullName = registerFullNameEt.getText().toString().trim();
        String username = registerUsernameEt.getText().toString().trim();
        String phone = registerPhoneEt.getText().toString().trim();
        String email = registerEmailEt.getText().toString().trim();
        String password = registerPasswordEt.getText().toString().trim();

        //Verify all fields are filled out correctly, username is unique and password follows
        //                                                                              requirements
        if (allFieldsAreFilled(fullName, username, phone, email, password) &&
                usernameIsAvailable(username) && passwordIsValid(password)) {
            attemptFirebaseRegistration(fullName, username, phone, email, password);
        }
    }

    /**
     * Verifies the given paramters are non-empty, if not sets an error on the TextView and alerts the user
     *
     * @param fullName the full name to be checked
     * @param username the username to be checked
     * @param phone    the phone to be checked
     * @param email    the email to be checked
     * @param password the password to be checked
     * @return
     */
    public boolean allFieldsAreFilled(String fullName, String username, String phone, String email,
                                      String password) {
        if (fullName.isEmpty()) {
            registerFullNameEt.setError("Full name is required");
            registerFullNameEt.requestFocus();
            return false;
        }
        if (username.isEmpty()) {
            registerUsernameEt.setError("Username is required");
            registerUsernameEt.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            registerPhoneEt.setError("Phone number is required");
            registerPhoneEt.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            registerEmailEt.setError("Email is required");
            registerEmailEt.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            registerPasswordEt.setError("Password is required");
            registerPasswordEt.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Searches all User objects in database looking for one with username equal to passed param.
     *
     * @param username the username to search for
     * @return true if username is unique, false if username is taken
     */
    public boolean usernameIsAvailable(String username) {
        DatabaseReference ref = FirebaseDatabase.getInstance("" +
                "https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("Users");

        ref.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            registerUsernameEt.setError("Username is already taken!");
                            registerUsernameEt.requestFocus();
                            usernameIsUnique = false;
                            Log.d("REGISTER", "Username already exists");
                        } else {
                            Log.d("REGISTER", "Username is unique");
                            usernameIsUnique = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        usernameIsUnique = false;
                        Log.d("REGISTER", error.getMessage());
                    }
                });
        return usernameIsUnique;
    }

    /**
     * Verifies the passed string is greater than or equal to 6 characters, and calls helper function
     * to check that password is in the correct format.
     *
     * @param password the string to be checked
     * @return true if password is valid, false if password is not
     */
    public boolean passwordIsValid(String password) {

        if (password.length() < 6) {
            registerPasswordEt.setError("Password must be at least 6 characters");
            registerPasswordEt.requestFocus();
            return false;
        }
        if (!ChangePasswordActivity.passwordFollowsFormat(password)) {
            registerPasswordEt.setError("Password must have at least one: capital letter, " +
                    "special character, and number");
            registerPasswordEt.requestFocus();
            return false;
        }
        return true;
    }


    public void attemptFirebaseRegistration(String fullName, String username, String phone, String email,
                                            String password) {
        //Attempt to create a new email/pass authentication account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Account created Successfully
                        if (task.isSuccessful()) {
                            //Create new User Object
                            User user = new User(fullName, username, phone, email);

                            //Attempt to store User object in FirebaseDatabase
                            FirebaseDatabase.getInstance("https://justudy-ebc7b-default-rtdb.europe-west1" +
                                    ".firebasedatabase.app").getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //SUCCESS: User posted to DB
                                    if (task.isSuccessful()) {
                                        sendVerificationEmail();
                                        Toast.makeText(RegisterActivity.this,
                                                "Success! To finish registration please check your email " +
                                                        "and follow the given " +
                                                        "instructions.", Toast.LENGTH_LONG).show();
                                        //Set Display Name on Firebase
                                        setFirebaseDisplayName(fullName);
                                        //Redirect to Login Page
                                        firebaseAuth.signOut();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    } else {
                                        //FAIL: User was NOT posted to DB
                                        Toast.makeText(RegisterActivity.this,
                                                "Failed to register! Try again!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            //Account FAILED to be created
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
    }

    /**
     * sendVerificationEmail(): sends a verification email to the current firebase user
     */
    private void sendVerificationEmail() {
        //Grab current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //Use firebase api to send verification email
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Email sent successfully
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + firebaseUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Error sending email
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Sets the firebase authentication accounts display name, note this is different
     * from the user object stored in realtime DB.
     *
     * @param fullName the full name to be set on the authentication account
     */
    private void setFirebaseDisplayName(String fullName) {
        //Grab current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //Create a ProfileChangeRequest variable to send to firebase
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName).build();

        //Call firebase api update profile function with proper variable
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Display name set successfully
                            Log.d("REGISTER", "Display Name Set Successfully!");
                        } else {
                            //Failed to set display name
                            Log.d("REGISTER", "Failed to set DisplayName");
                        }
                    }
                });
    }
}
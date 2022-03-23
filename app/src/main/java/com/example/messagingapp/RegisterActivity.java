package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    /** VARIABLES **/
    //Variables for references to activity_register.xml
    private Button registerBtn;
    private EditText registerFullNameEt, registerEmailEt, registerPasswordEt, registerUsernameEt;
    private EditText registerPhoneEt;

    //Variables for firebase auth
    private FirebaseAuth firebaseAuth;

    //Global variable for method use
    private boolean usernameIsUnique;

    /** onCreate() is a method that runs before a user see's the current activity
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
     * Makes an email/pass authentication account with firebase, and posts a new User object
     * to the firebase database.
     *
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
                    attemptFirebaseRegistration(fullName, username, phone, email,password);
                }
    }

    //TODO: Write contract
    public boolean allFieldsAreFilled(String fullName, String username, String phone, String email, String password) {
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

    //TODO: Write contract
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

    //TODO: Write contract
    public boolean passwordIsValid(String password) {

        if (password.length() < 6) {
            registerPasswordEt.setError("Password must be at least 6 characters");
            registerPasswordEt.requestFocus();
            return false;
        }
        if (!passwordFollowsFormat(password)) {
            registerPasswordEt.setError("Password must have at least one: capital letter, " +
                    "special character, and number");
            registerPasswordEt.requestFocus();
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
    private boolean passwordFollowsFormat(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{3,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void attemptFirebaseRegistration(String fullName, String username, String phone, String email, String password) {
        //Attempt to create a new email/pass authentication account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //Account created Successfully
                    if(task.isSuccessful()) {
                        //Create new User Object
                        User user = new User(fullName, username, phone, email);
                        //Attempt to store User object in FirebaseDatabase
                        FirebaseDatabase.getInstance("https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //SUCCESS: User posted to DB
                                if (task.isSuccessful()) {
                                    sendVerificationEmail();
                                    Toast.makeText(RegisterActivity.this,
                                            "Success! To finish registration please check your email and follow the given +" +
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
    private void sendVerificationEmail() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + firebaseUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                            Log.d("REGISTER", "Display Name Set Successfully!");
                        } else {
                            Log.d("REGISTER", "Failed to set DisplayName");
                        }
                    }
                });
    }
}
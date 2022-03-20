package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //Variables for references to activity_main.xml
    private Button loginBtn, googleSignInButton;
    private EditText emailEt, passwordEt;
    private TextView forgotTv, registerTv;

    //Firebase + Google Auth Variables
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    public FirebaseAuth firebaseAuth;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";


    /** onCreate() is a method that runs before a user see's a page
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default onCreate() operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find references from xml file
        registerTv = findViewById(R.id.registerTv);
        loginBtn = findViewById(R.id.btnLogin);
        emailEt = findViewById(R.id.inputEmail);
        passwordEt = findViewById(R.id.inputPassword);
        forgotTv = findViewById(R.id.forgotTv);
        googleSignInButton = findViewById(R.id.btnGoogle);

        //Set references On-Click Listeners
        registerTv.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgotTv.setOnClickListener(this);

        //Init Google Auth
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Check if a user is already signed in
        checkUser();
    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerTv:
                //Start the register new account activity
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btnGoogle:
                //begin the google sign in process
                Log.d(TAG, "onClick: begin Google SignIn");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
                break;
            case R.id.btnLogin:
                //begin the email + password login process
                loginWithEmailPass();
                break;
            case R.id.forgotTv:
                //Start the forgot password activity
                startActivity(new Intent(this, ForgotPassActivity.class));
                break;
        }
    }

    /**
     * Checks if a user is already logged in, and automatically redirects to profile activity
     *
     * @post if @code{firebaseUser != null} then move them to the profile activity
     *       if @code{firebaseUser == null} do nothing
     */
    private void checkUser() {
        //if user is already signed in then go to profile activity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "checkUser: User Already Signed In");
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }

    /**
     * loginUser(), logs a user in using the email + password specified in the EditText fields
     *
     * @modifies @code{firebaseAuth.getCurrentUser()}
     * @post  if SUCCESS @code{firebaseAuth.getCurrentUser() != NULL}
     *        if FAIL @code{firebaseAuth.getCurrentUser() == NULL}
     */
    public void loginWithEmailPass() {
        //Store Email & Password written in EditText fields
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if (verifyEmailPassFields(email, password)) {
            //Begin authentication of information with firebase
            firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Login SUCCESS: Redirect to Profile
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        } else {
                            //Login FAIL: Alert user of issue
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
        }
    }

    /**
     *  Verifies that Email + Pass fields are not empty, and the email is in a valid format
     *
     * @param email a string containing the user inputted email
     * @param password a string containing the user inputted password
     * @modifies none
     * @post  result=TRUE @code{!email.isEmpty() && !password.isEmpty() &&
     *                          Patterns.EMAIL_ADDRESS.matcher(email).matches()}
     *        result=FALSE @code{email.isEmpty() || !password.isEmpty() ||
     *                          !Patterns.EMAIL_ADDRESS.matcher(email).matches()}
     */
    public boolean verifyEmailPassFields(String email, String password){
        //Verify constraints for email and password
        if (email.isEmpty()) {
            emailEt.setError("Email is required!");
            emailEt.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordEt.setError("Password is required!");
            passwordEt.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Email is not in a valid format!");
            emailEt.requestFocus();
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned form launching Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google SignIn intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //SignIn Was Successful, Now Auth Firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);

            }
            catch (Exception e) {
                //Failed SignIn
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }


    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d(TAG, "onSuccess: Logged In");

                        //Get logged-in user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        //Get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d(TAG, "onSuccess: Email: " + email);
                        Log.d(TAG, "onSuccess: UID: " + uid);
                        //Check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            //User Is New - Account Creation
                            addGoogleAcctToDB(firebaseUser.getDisplayName(), firebaseUser.getUid().toString(), " ", email);
                            Log.d(TAG, "onSuccess: Account Created...\n" + email);


                            Toast.makeText(MainActivity.this, "Account Created...\n"
                                    + email, Toast.LENGTH_SHORT).show();
                        } else {
                            //existing user - Logged In
                            Log.d(TAG, "onSuccess: Existing User...\n" + email);
                            Toast.makeText(MainActivity.this, "Existing User...\n"
                                    + email, Toast.LENGTH_SHORT).show();
                        }
                        //Start profile activity
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Login Failed " + e.getMessage());
                    }
                });
    }

    /**
     * If an account is created via google sign in, we still need a reference to them in the DB, thus
     * this method does such.
     */
    private void addGoogleAcctToDB(String fullName, String username, String phone, String email ) {

        User user = new User (fullName, username, phone, email);
        FirebaseDatabase.getInstance("https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                /** User posted to database successfully **/
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            "User has been registered successfully!",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else {
                    /** User failed to be added to database **/
                    Toast.makeText(MainActivity.this,
                            "Failed to register! Try again!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
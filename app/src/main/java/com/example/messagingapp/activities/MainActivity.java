package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messagingapp.R;
import com.example.messagingapp.objects.User;
import com.example.messagingapp.utilities.LocationHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Variables for references to activity_main.xml
    private Button loginBtn, googleSignInButton, guestBtn, facebookBtn, microsoftBtn;
    private EditText emailEt, passwordEt;
    private TextView forgotTv, registerTv;

    //Firebase + Google Auth Variables
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    public FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    public static boolean isGuest = false;
    private boolean firstTime = true;

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 101;
    private double latitude;
    private double longitude;


    /**
     * onCreate() is a method that runs before a user see's a page
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
        guestBtn = findViewById(R.id.btnGuest);
        facebookBtn = findViewById(R.id.btnFacebook);
        microsoftBtn = findViewById(R.id.btnMicrosoft);

        //Set references On-Click Listeners
        registerTv.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgotTv.setOnClickListener(this);
        guestBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        microsoftBtn.setOnClickListener(this);

        //Init Google Auth
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //initialize fusedLocationProviderClient
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Check if a user is already signed in
        checkUser();

        //Checks if it is the first time user is opening the app
        firstTimeSetup();
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
                //Register new user, start register process
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btnGoogle:
                //Google sign in, start google login process
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
            case R.id.btnGuest:
                //Guest login, start profile activity
                isGuest = true;
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.btnFacebook:
                //Facebook login, start facebook login process
                Intent intentFacebook = new Intent(MainActivity.this, FacebookAuthActivity.class);
                intentFacebook.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentFacebook);
                break;
            case R.id.btnMicrosoft:
                //Microsoft login, start microsoft login process
                isGuest = false;
                signInWithMicrosoft();
                break;
        }
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        Map<String, Object> data = new HashMap<>();
        data.put("Token", token);
        firebaseFirestore.collection("Tokens")
                .document(firebaseAuth.getCurrentUser().getUid())
                .set(data)
                .addOnSuccessListener(unused -> Log.d("TOKEN", "UPDATED"))
                .addOnFailureListener(e -> Log.d("TOKEN", "Error Updating: " + e));
    }

    /**
     * Checks if a user is already logged in, and automatically redirects to profile activity
     *
     * @post if @code{firebaseUser != null} then move them to the profile activity
     * if @code{firebaseUser == null} do nothing
     */
    private void checkUser() {
        //if user is already signed in then go to profile activity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            getToken();
            Log.d(TAG, "checkUser: User Already Signed In");
            isGuest = false;
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }

    /**
     * Checks if a user has ever opened the app and asks for permission to use location if
     * permission is not yet granted
     *
     * @post if @code{firstTime == true} then firstTime = False
     */
    private void firstTimeSetup() {
        LocationHandler.askPermissionLoc(this);
        if (firstTime) {
            if (LocationHandler.checkPermission(getApplicationContext())) {
                return;
            } else {
                LocationHandler.askPermissionLoc(this);
            }
            firstTime = false;
        }
    }

    /**
     * loginUser(), logs a user in using the email + password specified in the EditText fields
     *
     * @modifies @code{firebaseAuth.getCurrentUser()}
     * @post if SUCCESS @code{firebaseAuth.getCurrentUser() != NULL}
     * if FAIL @code{firebaseAuth.getCurrentUser() == NULL}
     */
    public void loginWithEmailPass() {
        //Store Email & Password written in EditText fields
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if (verifyEmailPassFields(email, password)) {
            //Begin authentication of information with firebase
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Login SUCCESS: Redirect to Profile
                                getToken();
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    isGuest = false;
                                    startActivity(new Intent(MainActivity.this,
                                            ProfileActivity.class));
                                } else {
                                    firebaseAuth.signOut();
                                    Toast.makeText(MainActivity.this, "You must verify your email" +
                                            " before logging in!", Toast.LENGTH_LONG).show();
                                }
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
     * Verifies that Email + Pass fields are not empty, and the email is in a valid format
     *
     * @param email    a string containing the user inputted email
     * @param password a string containing the user inputted password
     * @modifies none
     * @post result=TRUE @code{!email.isEmpty() && !password.isEmpty() &&
     * Patterns.EMAIL_ADDRESS.matcher(email).matches()}
     * result=FALSE @code{email.isEmpty() || !password.isEmpty() ||
     * !Patterns.EMAIL_ADDRESS.matcher(email).matches()}
     */
    public boolean verifyEmailPassFields(String email, String password) {
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


    /**
     * A core function for google sign in, provided by firebase
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned form launching Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google SignIn intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //SignIn Was Successful, Now Auth Firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);

            } catch (Exception e) {
                //Failed SignIn
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }

    /**
     * A core function for google sign in, provided by firebase
     *
     * @param account
     */
    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d(TAG, "onSuccess: Logged In");
                        getToken();
                        isGuest = false;
                        //Get logged-in user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        //Get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        //Check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            //User Is New - Account Creation
                            addAcctToDB(firebaseUser.getDisplayName(),
                                    firebaseUser.getUid().toString(), " ", email);
                            //Inform user
                            Toast.makeText(MainActivity.this, "Account Created...\n"
                                    + email, Toast.LENGTH_SHORT).show();
                        } else {
                            //existing user - Logged In
                            Log.d(TAG, "onSuccess: Existing User...\n" + email);
                            //Inform user
                            Toast.makeText(MainActivity.this, "Existing User...\n"
                                    + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
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
     * If an account is created via google sign in, we still need a reference to them in the DB,
     * thus
     * this method does such.
     */
    private void addAcctToDB(String fullName, String username, String phone, String email) {

        User user = new User(fullName, username, phone, email);
        FirebaseDatabase.getInstance("https://justudy-ebc7b-default-rtdb.europe-west1" +
                ".firebasedatabase.app").getReference("Users")
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

    /**
     * Begins the microsoft login process
     */
    private void signInWithMicrosoft() {
        //Create OAuth variable for firebase to connect with microsoft
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");

        //Provided code from Firebase
        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask.addOnSuccessListener(new OnSuccessListener<AuthResult>
                    () {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.e("PROFILE", authResult.getAdditionalUserInfo()
                            .getProfile().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FAIL", "ERROR LOGIN");
                }
            });
        } else {
            firebaseAuth.startActivityForSignInWithProvider(MainActivity.this, provider.build())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            getToken();
                            isGuest = false;
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            //Check if a user is new
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                //Add account to firebase realtime database
                                addAcctToDB(" ", firebaseUser.getUid(), "0",
                                        firebaseUser.getEmail());
                            }
                            //Redirect to profile page
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Login with microsoft failed
                    Log.e("FAIL", "ERROR LOGIN: " + e);
                }
            });
        }
    }


}
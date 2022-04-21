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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class NewMessageActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * VARIABLES
     **/
    //References to XML file
    private Button newMessageBtn;
    private EditText usernameToChatWith;

    //String to hold username from Edit Text field
    private String receiverUsername;

    //Firebase Authentication and Database variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;

    /** METHODS **/

    /**
     * onBackPressed() Changes the functionality of the android os back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Start the messaging activity
        startActivity(new Intent(this, MessagesActivity.class));
    }

    /**
     * onCreate() is the first method that runs when the activity is started
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Assign the correct XMl file
        setContentView(R.layout.activity_new_message);

        //Init references to xml file
        newMessageBtn = findViewById(R.id.startChatBtn);
        usernameToChatWith = findViewById(R.id.newMessageUserET);

        //Init On-Click Listeners
        newMessageBtn.setOnClickListener(this);

        //Init firebase auth, firebase database, and firestore database
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("" +
                "https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app");
        firebaseFirestore = FirebaseFirestore.getInstance();


        /* When somebody clicks the contact button on a listing, it stores the
        needed full name and UID in the intent extras, thus this code checks
        if the extras are null, and if not registers the necesarry information in the database
        by calling startChatWithUser() */
        checkIntentExtras();

    }

    /**
     * checkIntentExtras(): checks if any extra strings are stored in intent, and calls the
     * necessary functions to complete the chat room creation. Note extras are stored in the intent
     * only when a user clicks the contact button a listing.
     */
    private void checkIntentExtras() {
        Bundle received = getIntent().getExtras();
        if (received != null) {
            String nakdasd = received.getString("contact");
            String parts[] = received.getString("contact").split(":");
            Log.d("filter", "userlistingactivity: " + String.valueOf(nakdasd));
            String person = parts[0];
            String userId = parts[1];
            checkUserExists(person);
        }
    }

    /**
     * Holds any on click listeners for references to objects in the XML file
     *
     * @param view the current view in the activity
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startChatBtn:
                receiverUsername = usernameToChatWith.getText().toString().trim();
                checkUserExists(receiverUsername);
                break;
        }
    }

    /**
     * checkUserExists(): checks if a given username exists and if found calls startChatWithUser()
     *
     * @param receiverUsername the username the current user wants to chat with
     */
    private void checkUserExists(String receiverUsername) {
        //References the users branch on our database
        DatabaseReference ref = firebaseDatabase.getReference("Users");

        //Attempt to find a User Class in database with passed String receiverUsername
        ref.orderByChild("username").equalTo(receiverUsername).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) { //Receiving user found
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                //Pull stored user class from database
                                User user = snapshot1.getValue(User.class);
                                //snapshot1.getKey() contains the receiving users UID
                                if (!snapshot1.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                    startChatWithUser(snapshot1.getKey(), user.fullName);
                                } else {
                                    Toast.makeText(NewMessageActivity.this,
                                            "You cannot chat with yourself", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else { //Receiving user not found, alert current user
                            Toast.makeText(NewMessageActivity.this,
                                    "This user does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //Database request was canceled.
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("REGISTER", error.getMessage());
                    }
                });
    }

    /**
     * Adds a given UID and full name to the current users list of all users they are chatting with.
     * Subsequently, the same must be done for the receiving user so their list is up to date.
     *
     * @param receiverUID  the UID to be added
     * @param receiverName the full name to be added
     */
    private void startChatWithUser(String receiverUID, String receiverName) {
        //Hash map to store data for the current users list
        Map<String, Object> userDataForSender = new HashMap<>();
        //Hash map to store data for the receiving users list
        Map<String, Object> userDataForReceiver = new HashMap<>();

        //Put receiving users info into hashmap to store on current users list
        userDataForSender.put("name", receiverName);
        userDataForSender.put("uid", receiverUID);

        //Put current users info into hashmap to store on receiving users list
        userDataForReceiver.put("name", firebaseAuth.getCurrentUser().getDisplayName());
        userDataForReceiver.put("uid", firebaseAuth.getCurrentUser().getUid());

        //Find the correct collection and create document to store receiving users info
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("ReceivingUsers")
                .document(receiverUID)
                .set(userDataForSender)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Find collection and create document to store current users info
                        firebaseFirestore.collection("Users").document(receiverUID)
                                .collection("ReceivingUsers")
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .set(userDataForReceiver)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(NewMessageActivity.this,
                                                MessagesActivity.class));
                                    }
                                });
                    }
                });
    }
}
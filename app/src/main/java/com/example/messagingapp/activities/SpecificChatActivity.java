package com.example.messagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messagingapp.adapters.RecycleSpecificChatAdapter;
import com.example.messagingapp.objects.Message;
import com.example.messagingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SpecificChatActivity extends AppCompatActivity implements View.OnClickListener {

    /** VARIABLES **/
    //Variables for References to XML
    EditText messageET;
    ImageButton backBtn;
    ImageView sendMessageBtn, attachImageBtn;
    CardView sendMessageCardView;
    Toolbar specificChatToolbar;
    TextView receiverNameTV;
    RecyclerView recyclerView;


    //variable to hold message user intends to send
    private String messageToSend;
    private File image;

    //Variables to store info about sender/reciever
    String receiverName, senderName, receiverUID, senderUID, currentTime;
    String receiverRoom, senderRoom;
    ArrayList<Message> messages;

    //Date Variables
    Calendar calender;
    SimpleDateFormat simpleDateFormat;

    //Firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //Adapter + Linear layout for Recycler
    RecycleSpecificChatAdapter specificChatAdapter;

    //Additional Variables
    Intent intent;

    /** METHODS **/

    /** onCreate() is a method that runs before a user see's the current activity
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Assign proper XMl file
        setContentView(R.layout.activity_specific_chat);

        //Init references to XML file
        messageET = findViewById(R.id.messageET2);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        backBtn = findViewById(R.id.backButtonSpecificChat);
        sendMessageCardView = findViewById(R.id.cardViewSpecificChatSendMessage);
        specificChatToolbar = findViewById(R.id.toolbarSpecificChat);
        receiverNameTV = findViewById(R.id.receiverNameTV);
        recyclerView = findViewById(R.id.recyclerSpecificChat);
        attachImageBtn = findViewById(R.id.attachImageBtn);

        //setup calender and date format for messages
        calender = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm");

        //setup intent so we can string extras from it
        intent = getIntent();
        setSupportActionBar(specificChatToolbar);

        //Init firebase auth and databse
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://justudy-ebc7b-default-rtdb.europe-west1" +
                ".firebasedatabase.app");

        //Grab sender/reciever name + UID
        senderName = firebaseAuth.getCurrentUser().getDisplayName();
        senderUID = firebaseAuth.getUid();
        receiverUID = getIntent().getStringExtra("receiverUID");
        receiverName = getIntent().getStringExtra("name");

        //Create strings to represent chat rooms
        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        //Setup On-Click Listeners
        backBtn.setOnClickListener(this);
        sendMessageBtn.setOnClickListener(this);
        attachImageBtn.setOnClickListener(this);

        //Set name of recieving user on top of screen
        receiverNameTV.setText(receiverName);

        //Init array list to hold messages
        messages = new ArrayList<>();

        //Init Recycler stuff
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        specificChatAdapter = new RecycleSpecificChatAdapter(SpecificChatActivity.this, messages);
        linearLayoutManager.setStackFromEnd(true); //This makes sure the older messages are at the top
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(specificChatAdapter);

        //Init firebase database reference
        databaseReference = firebaseDatabase
                .getReference("Chats")
                .child(senderRoom)
                .child("Messages");

        //Get all previous messages or new messages from database
        pullMessagesFromDatabase();


    }

    /**
     * Overrides the default android os back button functionality
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, MessagesActivity.class));
    }

    /**
     * Method that runs on activity start
     */
    @Override
    public void onStart() {
        super.onStart();
        //Inform chat adapter for recycler to update information
        specificChatAdapter.notifyDataSetChanged();
    }

    /**
     * Method that runs on activity stop
     */
    @Override
    public void onStop() {
        super.onStop();
        if (specificChatAdapter != null) {
            //Inform chat adapter for recycler to update information
            specificChatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * pullMessagesFromDataBase() : grabs all messages stored on database and stores them
     * in the messages ArrayList
     *
     * @modifies messages
     * @post if any previous messages exist, messages will contain them
     */
    private void pullMessagesFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Message message = snapshot1.getValue(Message.class);
                    messages.add(message);
                }
                specificChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param view a view of all elements present on the screen
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButtonSpecificChat:
                //finish activity and go back
                finish();
                startActivity(new Intent(this, MessagesActivity.class));
                break;
            case R.id.sendMessageBtn:
                //pull message from edit text field
                messageToSend = messageET.getText().toString();
                if (messageToSend.isEmpty()) {
                    //Inform user message is empty
                    Toast.makeText(getApplicationContext(), "Please enter a message first!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(messageToSend, null);
                }
                break;
            case R.id.attachImageBtn:
                //Begin image upload process
                selectImage();
                break;
        }
    }

    /**
     * Creates a prompt for the user to select how they would like to upload their image and
     * then starts the correct android activity.
     */
    private void selectImage() {
        final CharSequence[] options = {"Choose image from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SpecificChatActivity.this);
        builder.setTitle("Add an image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                } else if (options[i].equals("Choose image from gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            }
        });
        builder.show();
    }

    /**
     * Listens for an activity result and then performs action based on request code
     * ** currently being used to see what option the user selected when uploading image.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                image = new File(selectedImage.getPath());
                sendMessage("", selectedImage);
            }
        }
    }

    /**
     * Stores the passed file with name UID in the firebase cloud storage database
     * @param file a Uid representing the location of the file
     * @param UID a unique identifier to find the image, this should be the message UID
 *
     */
    private void storeImage(Uri file, String UID) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://justudy-ebc7b.appspot.com");
        StorageReference storageReference = storage.getReference();
        StorageReference storageReference1 = storageReference.child(UID);
        UploadTask uploadTask = storageReference1.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Nothing to do, image was uploaded succesfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Image upload failed, inform user
                Toast.makeText(SpecificChatActivity.this, "Failed to send image",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String messageToSend, Uri file) {
        //Pull current time for time stamp
        Date date = new Date();
        currentTime = simpleDateFormat.format(calender.getTime());

        //Create a message object with correct params
        Message message;
        if (file != null) {
             message = new Message(firebaseAuth.getUid(), date.getTime(), currentTime);
             //store the message under its messageID
            storeImage(file, message.getUniqueID());
        } else {
            message = new Message(messageToSend, firebaseAuth.getUid(), date.getTime(), currentTime);
        }
            //Post both the sender + receiver rooms to firebase database
            firebaseDatabase.getReference("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .push()
                    .setValue(message)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseDatabase.getReference("Chats")
                                        .child(receiverRoom)
                                        .child("Messages")
                                        .push()
                                        .setValue(message)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.d("CHAT", "Failed to post receiver room to DB");
                                                }
                                            }
                                        });
                            } else {
                                Log.d("CHAT", "Failed to post sender room to DB");
                            }
                        }
                    });
            //Clear text field so they can send another message
            messageET.setText(null);
    }
}
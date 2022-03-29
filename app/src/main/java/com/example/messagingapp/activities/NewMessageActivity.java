package com.example.messagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messagingapp.R;
import com.example.messagingapp.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
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

    private Button newMessageBtn;
    private EditText usernameToChatWith;
    private String receiverUsername;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        newMessageBtn = findViewById(R.id.startChatBtn);
        usernameToChatWith = findViewById(R.id.newMessageUserET);

        newMessageBtn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("" +
                "https://justudy-ebc7b-default-rtdb.europe-west1.firebasedatabase.app");
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startChatBtn:
                receiverUsername = usernameToChatWith.getText().toString().trim();
                checkUserExists();
                break;
        }
    }

    private void checkUserExists() {
        DatabaseReference ref = firebaseDatabase.getReference("Users");

        ref.orderByChild("username").equalTo(receiverUsername).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            User user = snapshot1.getValue(User.class);
                            startChatWithUser(snapshot1.getKey(), user.fullName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("REGISTER", error.getMessage());
                    }
                });
        }


    private void startChatWithUser(String receiverUID, String receiverName) {
        Map<String, Object> userDataForSender = new HashMap<>();
        Map<String, Object> userDataForReceiver = new HashMap<>();
        userDataForSender.put("name", receiverName);
        userDataForSender.put("uid", receiverUID);

        userDataForReceiver.put("name", firebaseAuth.getCurrentUser().getDisplayName());
        userDataForReceiver.put("uid", firebaseAuth.getCurrentUser().getUid());


        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("ReceivingUsers")
                .document(receiverUID)
                .set(userDataForSender)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseFirestore.collection("Users").document(receiverUID)
                                .collection("ReceivingUsers")
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .set(userDataForReceiver)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(NewMessageActivity.this, MessagesActivity.class));
                                    }
                                });
                    }
                });



    }

}
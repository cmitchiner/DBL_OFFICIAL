package com.example.messagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.messagingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MessagesActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    /** VARIABLES **/
    //Variables for references to XML
    BottomNavigationView bottomNavigationView;
    ImageView newMessageBtn;

    /** METHODS **/

    /**
     * onBackPressed() Changes the functionality of the android os back button
     */
    @Override
    public void onBackPressed() {
        return;
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
        //Set XML reference
        setContentView(R.layout.activity_messages);

        //Init references to XML
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.messages);
        newMessageBtn = findViewById(R.id.newMessageBtn);

        //Start fragment to hold all users you are chatting with
        ChatListFragment chatListFragment = new ChatListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.chat_list_frame_layout, chatListFragment);
        fragmentTransaction.commit();

        //Init on click listener for button to start chat
        newMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagesActivity.this, NewMessageActivity.class));
            }
        });
    }


    /**
     * Controls the bottom navigation bar
     * @param item holds list of 3 item choices on bottom nav bar
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Listing_Activity.class));
                return true;
            case R.id.profileNavBar:
                startActivity(new Intent(this,ProfileActivity.class));
                return true;
            case R.id.messages:
                return true;
        }
        return false;
    }

}
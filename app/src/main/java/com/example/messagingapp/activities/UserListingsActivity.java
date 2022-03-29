package com.example.messagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.example.messagingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserListingsActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    String person;
    String userId;
    String titleTypeInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listings);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.profileNavBar);


        Bundle received  = getIntent().getExtras();
        if(received != null){
            String nakdasd =received.getString("title");
            String parts[] = received.getString("title").split(":");
            Log.d("filter", "userlistingactivity: " + String.valueOf(nakdasd));
           person = parts[0];
           userId = parts[1];
        }



        Log.d("filter", userId);
        String titleTypeInfo = person + ":" + userId;

        listing_list listing_list = new listing_list();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("title", titleTypeInfo);
        listing_list.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_layout, listing_list);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this,Listing_Activity.class));
                return true;
            case R.id.profileNavBar:
                return true;
            case R.id.messages:
                startActivity(new Intent(this, MessagesActivity.class));
                return true;

        }

        return false;
    }
}
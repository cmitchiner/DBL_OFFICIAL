package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.messagingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Class to display listings owned by 1 specific user
 */
public class UserListingsActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    String person;
    String userId;
    String titleTypeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listings);

        // Initialize navbar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.profileNavBar);

        // Get passed parameters
        Bundle received = getIntent().getExtras();
        if (received != null) {
            String parts[] = received.getString("title").split(":");
            person = parts[0];
            userId = parts[1];
        }

        String titleTypeInfo = person + ":" + userId;
        // Create new listing list
        listing_list listing_list = new listing_list();
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Start a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Pass parameters
        Bundle bundle = new Bundle();
        bundle.putString("title", titleTypeInfo);
        listing_list.setArguments(bundle);
        // Set listing_list as new fragment
        fragmentTransaction.replace(R.id.frame_layout, listing_list);
        fragmentTransaction.commit();

    }

    /**
     * Defines navigation bar behavior
     *
     * @param item the item to navigate to
     * @return true iff the navigation item is a valid item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Listing_Activity.class));
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
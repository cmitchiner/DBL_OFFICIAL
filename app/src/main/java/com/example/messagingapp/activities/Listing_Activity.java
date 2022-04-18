package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.messagingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

/**
 * Activity which main job is to be a fragment container for listing_list or listing_opened and the navbar
 */
public class Listing_Activity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_activity);
        // Add navbar and bind navigation listener
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        String titleTypeInfo = "Offers:" + "no" ;
        // Create a new listing_list activity and put it into the fragment placeholder
        listing_list listing_list = new listing_list();
        FragmentManager fragmentManager = getSupportFragmentManager();
        //Start fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Pass title to the listing list
        Bundle bundle = new Bundle();
        bundle.putString("title", titleTypeInfo);
        listing_list.setArguments(bundle);
        //Put listing list into fragment holder
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

        switch(item.getItemId()) {
            case R.id.home:
                return true;
            case R.id.profileNavBar:
                startActivity(new Intent(this,ProfileActivity.class));
                return true;
            case R.id.messages:
                if (MainActivity.isGuest) {
                    Toast.makeText(this, "This feature is not available for " +
                            "guests!", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    startActivity(new Intent(this, MessagesActivity.class));
                }
                return true;

        }
        return false;

    }


}
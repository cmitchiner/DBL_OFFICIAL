package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, View.OnClickListener {

    BottomNavigationView bottomNavigationView;
    ImageView settingsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        settingsBtn = findViewById(R.id.settingsBtn);

        settingsBtn.setOnClickListener(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.profileNavBar);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.settingsBtn:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.profileNavBar:
                return true;
            case R.id.messages:
                startActivity(new Intent(this,MessagesActivity.class));
                return true;

        }
        return false;
    }
}
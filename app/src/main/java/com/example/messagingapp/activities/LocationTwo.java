package com.example.messagingapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationTwo extends AppCompatActivity{
    int PERMISSION_ID = 101;
    private double latitude;
    private double longitude;
    Location userLocation;
    //initialize fusedLocationProviderClient
    FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    /**
     * Checks if the user already has granted permission to use the location
     *
     * @post if the user has granted permission, return true
     */
    public boolean checkPermission() {

        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Asks the user for permission to use location
     */
    public void askPermissionLoc() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    /**
     * Checks if location is turned on by device
     */
    public boolean locationEnabled() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    /**
     * Requests location data
     */
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = LocationRequest.create();
        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());
    }

    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Something went wrong";
    }

    /**
     * Obtains the location from a user
     */
    @SuppressLint("MissingPermission")
    public android.location.Location getLocation(Context context, Map<String, ArrayList<String>> filtDict) {
        android.location.Location location = new android.location.Location("location");
        if (checkPermission()) {
            if (locationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                    @Override
                    public void onComplete(@NonNull Task<android.location.Location> task) {
                        android.location.Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //store to database here
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            userLocation = new Location("");
                            userLocation.setLatitude(latitude);
                            userLocation.setLongitude(longitude);
                            Log.d("filter", "userLocation: " + String.valueOf(userLocation));
                            //Toast.makeText(AddListingActivity.this, "Location: " + location, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, getAddress(latitude, longitude), Toast.LENGTH_LONG).show();
                            if(!filtDict.get("location").contains(latitude + ";" + longitude)){
                                filtDict.get("location").add(latitude + ";" + longitude);
                                Log.d("filter", "dict after location filt: " + String.valueOf(filtDict));
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        } else {
        askPermissionLoc();
        }
        return userLocation;
    }
}

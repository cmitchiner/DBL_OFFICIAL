package com.example.messagingapp.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHandler extends AppCompatActivity {
    //int to store the ID of the location permission
    static int PERMISSION_ID = 101;

    /**
     * Checks if the user already has granted permission to use the location
     *
     * @post if the user has granted permission, return true
     * else, return false
     */
    public static boolean checkPermission(Context context) {

        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Asks the user for permission to use location with a pop up where the user can select
     * precise location or rough location
     */
    public static void askPermissionLoc(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    /**
     * Checks if location is turned on by device
     *
     * @post returns true if location is enabled
     * else returns false
     */
    public static boolean locationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Returns the Address of the user in the form of a pop up
     *
     * @param context  current app location context
     * @param location current location of user
     */
    public static String getAddress(Context context, Location location) {
        Geocoder geocoder;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
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
     *
     * @param context  current app location context
     * @param activity current activity being used
     * @param listener listener to call a method as soon as location is changed
     */
    @SuppressLint("MissingPermission")
    public static void getLocation(Context context, Activity activity, onLocationListener listener) {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        //checks if the permission to access the location is given
        if (LocationHandler.checkPermission(context)) {
            //checks if the location of the user is enabled
            if (LocationHandler.locationEnabled(context)) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        //checks if obtaining the location succeeded
                        if (location == null) {
                            throw new RuntimeException("Location services failed, please try again");
                        }
                        listener.onLocation(location);
                    }
                });
                //if the location of the user is not enabled it requests the user to enable it
            } else {
                Toast.makeText(context, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
            //if the user has not yet granted permission to access the location data, the user is requested
            // to give permission
        } else {
            askPermissionLoc(activity);
            throw new RuntimeException("No permissions given");
        }
    }

    /**
     * Converts a location to a specifically formatted string used in this apps database
     *
     * @param location the location object to format
     * @return a string with the specified format
     */
    public static String toString(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        return String.format(Locale.ROOT, "%010.5f;%010.5f", latitude, longitude);
    }

    /**
     * Converts a string with specified format to a location object
     *
     * @param string the string to be converted
     * @return location object from the string
     */
    public static Location fromString(String string) {
        //checks if the format of the location data is correct
        if (!string.matches("-?[0-9]{3,4}\\.[0-9]{5};-?[0-9]{3,4}\\.[0-9]{5}")) {
            throw new RuntimeException("String format invalid");
        }
        Location loc = new Location("");
        String[] coords = string.split(";");
        loc.setLatitude(Double.valueOf(coords[0]));
        loc.setLongitude(Double.valueOf(coords[1]));
        return loc;
    }

    /**
     * Define a listener for when location gets returned in getLocation()
     */
    public interface onLocationListener {
        public void onLocation(Location location);
    }
}

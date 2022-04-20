package com.example.messagingapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.ApiAccess;
import com.example.messagingapp.R;
import com.example.messagingapp.adapters.RecycleOfferAdapter;
import com.example.messagingapp.SelectListener;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.protobuf.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link listing_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listing_list extends Fragment {
    int count = 0;

    //Components
    TextView titleView;
    RecyclerView recycler;
    RecycleOfferAdapter recycleOfferAdapter;
    ProgressBar progressBar;
    NestedScrollView nested_scroll;
    ImageButton addListingButton;
    Spinner spinner;
    AutoCompleteTextView filterText;
    Button locationbutt;

    String filtCol;
    String filtContent;
    String personalType;
    String userId;
    Location userLocation;

    //Arraylists for items for recyclerview
    Map<String, ArrayList<String>> filtDict;
    ArrayList<ListFacade> list = new ArrayList<>();

    //Listner interface
    SelectListener selectListener;

    //Retrofit interface
    ApiAccess apiAccess;

    //Location varibles
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 101;
    private double latitude;
    private double longitude;
    //private Location location;
    Location location = new Location("location");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public listing_list() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment listing_list.
     */
    // TODO: Rename and change types and number of parameters
    public static listing_list newInstance(String param1, String param2) {
        listing_list fragment = new listing_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Creating retrofit object
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.apiBaseUrl)).addConverterFactory(GsonConverterFactory.create()).build();
        apiAccess = retrofit.create(ApiAccess.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_list,
                container, false);
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initiating variables, arrays and components
        initViewsAndVars(view);

        //Creating dictionary for filtering and instantiating arrays per filter type
        Log.d("filter", String.valueOf(getArguments()));
        filtDict = new HashMap<>();
        filtDict.put("author", new ArrayList<String>());
        filtDict.put("title", new ArrayList<String>());
        filtDict.put("type",    new ArrayList<String>());
        filtDict.put("university", new ArrayList<String>());
        filtDict.put("course_code", new ArrayList<String>());
        filtDict.put("isbn", new ArrayList<String>());
        filtDict.put("location", new ArrayList<String>());
        Log.d("filter", String.valueOf(filtDict));


        //Set Title and add filters, depending on how listing list was started
        if(getArguments() != null){

            Log.d("bundle", String.valueOf(getArguments()));
            personalType = getArguments().getString("title");
            String[] parts = personalType.split(":");
            userId = parts[1];

            titleView.setText(String.valueOf(parts[0]+"'s Offers"));

            //If listing list is started from profile, or from opened listing:
            if( !userId.toString().equals("no") ) {
                titleView.setText(String.valueOf(parts[0]+"'s Offers"));
                filtDict.get("author").add(userId);
                Log.d("filter", String.valueOf(filtDict));
                filter();

                //If listing list is stated from nav bar:
            }else {
                titleView.setText(String.valueOf("Offers"));
            }
        }

        //Setting up spinner
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Creating Spinner and setting adapter for filter column selection
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity()
                        , R.array.filterColumns, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                //Onclick listener to set chosen spinner item as the filter type
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filtCol = (adapterView.getItemAtPosition(i).toString().toLowerCase());
                        Log.d("filter", "filtCol: " + filtCol);
                        //Makes spinner text white

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //Setting up text view for filtering

                //Adding event listener for software keyboard input
                filterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if(i == EditorInfo.IME_ACTION_SEARCH) {

                            filtContent = textView.getText().toString().trim();
                            Log.d("filter", "kur");
                            if(!filtContent.isEmpty()) {
                                if(!filtDict.get(filtCol).contains( filtContent) || !filtDict.get("location").isEmpty() ){
                                    Addbubble(filtCol+":"+filtContent);
                                    filtDict.get(filtCol).add(filtContent);
                                    filter();
                                } else{
                                    Toast.makeText(getActivity(), "Already filtering by " + filtContent, Toast.LENGTH_SHORT).show();
                                }

                                Log.d("filter", String.valueOf(filtDict));
                            }
                            return true;
                        }
                        filterText.setText("");
                        filterText.clearFocus();
                        return false;
                    }
                });

            }
        }).start();

        //Setting Add Listing button, which starts the addListingActivity
        addListingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.isGuest) {
                    //Deny add listing capability to guests
                    Toast.makeText(getActivity(), "This feature is not allowed for guests", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getContext(), AddListingActivity.class));
                }
            }
        });

        //Adding listings, when listing list has been started from nav bar
        if(userId.toString().equals("no")) {
            filter();
        }

        //Add more listings when the user scrolls down
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    count++;
                    progressBar.setVisibility(View.VISIBLE);
                    if(count < 100){
                        filter();
                    }
                }
            }
        });

    }

    /**
     * Method that initiates View and variables
     * @param view
     */
    private void initViewsAndVars(View view) {
        recycler = view.findViewById(R.id.offerContainer);
        progressBar = view.findViewById(R.id.idPBLoading);
        nested_scroll = view.findViewById(R.id.nested_scroll);
        addListingButton = view.findViewById(R.id.add_offer_butt);
        spinner = view.findViewById(R.id.filterCol);
        filterText = view.findViewById(R.id.filterInput);
        titleView = view.findViewById(R.id.listingListTitle);
        locationbutt = view.findViewById(R.id.locationFiltButt);
        filtDict = new HashMap<>();
        //OnClick listener for RecyclerView rows
        selectListener = new SelectListener() {
            @Override
            public void onItemClicked(ListFacade listFacade) {
                rowOnClick(listFacade);
            }
        };

        //OnClick listener for location button
        //Gets the current location and creates a filter bubble
        locationbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                Addbubble("location:Within 5km");
                filter();
            }
        });
    }

    /**
     * Pushes filter dictionary to the server, then pulls listings based on the filters and
     * refreshes the current listings on screen
     * @param filtDict - contains the filters
     */
    public void pushDictionary(Map<String, ArrayList<String>> filtDict){
        Log.d("filter", "this triggers: "+String.valueOf(filtDict));
        Call<ArrayList<ListFacade>> pushDict = apiAccess.getFilteredInfo(getResources().getString(R.string.apiDevKey), filtDict);
        pushDict.enqueue(new Callback<ArrayList<ListFacade>>() {
            @Override
            public void onResponse(Call<ArrayList<ListFacade>> call, Response<ArrayList<ListFacade>> response) {
                if(!response.isSuccessful()){
                    return;
                }

                //Pushing the result of the pull request to the listFacade array list
                list = response.body();

                //Filtering by location by comparing current user and item locations
                if(!filtDict.get("location").isEmpty()){
                    ArrayList<ListFacade> toRemove = new ArrayList<>();
                    for(ListFacade item:list){
                        Location loc;
                        if(item.getLocation() == null){
                            toRemove.add(item);
                        } else {
                            loc = new Location("");
                            String[] coords = item.getLocation().split(";");
                            loc.setLatitude(Double.valueOf(coords[0]));
                            loc.setLongitude(Double.valueOf(coords[1]));
                            float[] distance = new float[2];

                            //Calculating distance between user and item location in meters
                            Location.distanceBetween(loc.getLatitude(), loc.getLongitude(),
                                    userLocation.getLatitude(), userLocation.getLongitude(),
                                    distance);
                            if(distance[0] > 5000){
                                toRemove.add(item);
                            }
                        }
                    }
                    list.removeAll(toRemove);
                }
                //Adding rows to recyclerView
                recycleOfferAdapter = new RecycleOfferAdapter(getActivity(), list, selectListener);
                recycler.setAdapter(recycleOfferAdapter);

            }

            @Override
            public void onFailure(Call<ArrayList<ListFacade>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //Method to get the current location of the user
    @SuppressLint("MissingPermission")
    private Location getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (checkPermission()) {
            if (locationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            //store to database here
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            userLocation = new Location("");
                            userLocation.setLatitude(latitude);
                            userLocation.setLongitude(longitude);
                            Toast.makeText(getContext(), getAddress(latitude, longitude), Toast.LENGTH_LONG).show();
                            if(!filtDict.get("location").contains(latitude + ";" + longitude)){
                                filtDict.get("location").add(latitude + ";" + longitude);
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        }else {
            askPermissionLoc();
        }
        return location;
    }

    /**
     * On click listener for the RecyclerView rows.Swaps fragments when a row is clicked.
     * Requests the full listing data before opening the fragment
     * @param listFacade
     */
    public void rowOnClick(ListFacade listFacade) {
        Call<ResponseBody> getFullData = apiAccess.getDetailedListing(listFacade.getList_iD(),
                getResources().getString(R.string.apiDevKey) );
        getFullData.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                JSONObject data;
                try {
                    String extraData = response.body().string();
                    data = new JSONObject(extraData);
                } catch (Exception e) {
                    return;
                }

                try {
                    ArrayList<String> photos = new ArrayList<>();
                    photos.add(data.optString("photos"));
                    String loc;
                    if (listFacade.getLocation() != null) {
                        loc = listFacade.getLocation();
                    } else {
                        loc = null;
                    }

                    Listing list;

                    if (listFacade.getType().toLowerCase().equals("book")) {
                         list = new Listing(listFacade.getList_iD(), photos, listFacade.getPrice(), listFacade.getType(), data.optInt("reports"),
                                data.optBoolean("sold"), listFacade.getTitle(),listFacade.getIsbn(),loc,
                                data.optString("lang"), data.optString("aucid"), data.optString("description"), listFacade.getUniversity(),
                                listFacade.getCourseCode(), data.optString("ownerid"));

                    } else {
                         list = new Listing(listFacade.getList_iD(), photos, listFacade.getPrice(), listFacade.getType(), data.optInt("reports"),
                                data.optBoolean("sold"), listFacade.getTitle(),loc,
                                data.optString("lang"), data.optString("aucid"), data.optString("description"), listFacade.getUniversity(),
                                listFacade.getCourseCode(), data.optString("ownerid"));
                    }

                    openListing(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                return;
            }
        });
    }

    /**
     * Swaps listing list fragment with the opened listing fragment, depending on the value of isBid
     * @param list - Listing object passed as a parameter to the corresponding fragment
     * @post A new fragment is opened, with data taken from the list parameter
     */
    public void openListing(Listing list) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("listingFacade", list);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(!list.getIsBid()) {
            listing_opened listing_opened = new listing_opened();
            listing_opened.setArguments(bundle);

            fragmentTransaction.replace(R.id.frame_layout, listing_opened, "listingId").addToBackStack(null);
            fragmentTransaction.commit();

        } else if(list.getIsBid()) {
            listing_opened_bid listing_opened_bid = new listing_opened_bid();
            listing_opened_bid.setArguments(bundle);

            fragmentTransaction.replace(R.id.frame_layout, listing_opened_bid, "listingId").addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    /**
     * Method to create the ui bubble after entering a filter in the searchbar
     * @param query- string representing the text input from the AutoCompleteTextView
     */
    public void Addbubble(String query) {
        LinearLayout filt_cont  = (LinearLayout) getView().findViewById(R.id.filt_bubble_cont);
        View bubble = getLayoutInflater().inflate(R.layout.fiter_tag_bubble, filt_cont,
                false);
        TextView bubble_text = (TextView) bubble.findViewById(R.id.bubble_text);
        bubble_text.setText(query);

        //OnClick listener to remove bubble and the corresponding filter
        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence bubText = bubble_text.getText();
                String parts[] = bubText.toString().split(":");
                if(parts[0].equals("location")) {
                    filtDict.get(parts[0]).remove(0);
                } else{
                    filtDict.get(parts[0]).remove(parts[1]);
                }
                filt_cont.removeView(v);
                //Refresh rows
                filter();
            }
        });
        filt_cont.addView(bubble);
    }

    //Function to filter listings
     public void filter() {
        pushDictionary(filtDict);

    }




    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Something went wrong";
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = LocationRequest.create();
        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());
    }

    public boolean locationEnabled() {
        LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    public boolean checkPermission() {

        return ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void askPermissionLoc() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }



}



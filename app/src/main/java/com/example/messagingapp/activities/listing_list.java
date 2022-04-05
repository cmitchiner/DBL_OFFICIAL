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

        //Set Title and add filter, depending on how listing list was started
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

        if(getArguments() != null){

            Log.d("bundle", String.valueOf(getArguments()));
            personalType = getArguments().getString("title");
            String[] parts = personalType.split(":");
            userId = parts[1];

            titleView.setText(String.valueOf(parts[0]+"'s Offers"));
            if( !userId.toString().equals("no") ) {
                titleView.setText(String.valueOf(parts[0]+"'s Offers"));
                filtDict.get("author").add(userId);
                Log.d("filter", String.valueOf(filtDict));
                filter();

            }else {
                titleView.setText(String.valueOf("Offers"));
            }
        }

        //Setting up spinner
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Creating Spinner for filter column selection
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity()
                        , R.array.filterColumns, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filtCol = (adapterView.getItemAtPosition(i).toString().toLowerCase());
                        Log.d("filter", "filtCol: " + filtCol);
                        //Makes spinner text white
        /*switch (filtCol){
            String[] empty;
            case "Title":
                filterText.setAdapter(new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1,  ));
                break;
            case "Type":
                String[] types = {"book", "notes", "summary"};
                filterText.setAdapter(new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, types));
                break;
            case "University":
                String[] university = getResources().getStringArray(R.array.Universities);
                filterText.setAdapter(new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, university));
                break;
            case "Course code":
                break;
            case "ISBN":
                break;
        }
        filterText.showDropDown();

         */

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //Making the drop down menu show up on text field click
                filterText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //filterText.showDropDown();
                    }
                });


                //Setting up text view for filtering

                //Adding event listner for soft input
                filterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if(i == EditorInfo.IME_ACTION_SEARCH) {

                            filtContent = textView.getText().toString().trim();
                            Log.d("filter", "kur");
                            if(!filtContent.isEmpty()) {
                                if(!filtDict.get(filtCol).contains(filtContent)){
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

        //Setting Add Listing button
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

        //initializing arrays
        if(userId.toString().equals("no")) {
            getData();
        }

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
        selectListener = new SelectListener() {
            @Override
            public void onItemClicked(ListFacade listFacade) {
                rowOnClick(listFacade);
            }
        };

        locationbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                Addbubble("location:Within 5km");
                filter();
            }
        });
    }

    private void getData(){
        int rowNum = 150;

        Call<ArrayList<ListFacade>> listingQuery = apiAccess.getInfo(getResources().getString(R.string.apiDevKey), rowNum);
        listingQuery.enqueue(new Callback<ArrayList<ListFacade>>() {
            @Override
            public void onResponse(Call<ArrayList<ListFacade>> call, Response<ArrayList<ListFacade>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getContext(), "no response ", Toast.LENGTH_SHORT).show();
                    return;
                }
                list = response.body();
                recycleOfferAdapter = new RecycleOfferAdapter(getActivity(), list, selectListener);
                recycler.setAdapter(recycleOfferAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<ListFacade>> call, Throwable t) {
                Log.d(null, t.getMessage());
            }
        });
    }


    //On click listner for the rows. Requests the full listing data before opening the fragment
    public void rowOnClick(ListFacade listFacade) {
        Call<ResponseBody> getFullData = apiAccess.getDetailedListing(listFacade.getList_iD(),getResources().getString(R.string.apiDevKey) );
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
//                    JSONArray array = data.optJSONArray("photos");
//                    if(array != null) {
//                        for(int i = 0; i < array.length(); i++) {
//                            photos.add(array.getString(i));
//                        }
//                    }
                    Location loc;
                    if (listFacade.getLocation() != null) {
                        loc = new Location("");
                        String[] coords = listFacade.getLocation().split(";");
                        loc.setLatitude(Double.valueOf(coords[0]));
                        loc.setLongitude(Double.valueOf(coords[1]));
                    } else {
                        loc = null;
                    }


                    Listing list;

                    if (listFacade.getType().toLowerCase().equals("book")) {

                         list = new Listing(listFacade.getList_iD(), photos, listFacade.getPrice(), listFacade.getType(), data.optInt("reports"),
                                data.optBoolean("sold"), listFacade.getTitle(),listFacade.getIsbn(),loc,
                                data.optString("lang"), data.optString("aucid"), data.optString("description"), listFacade.getUniversity(),
                                listFacade.getCourseCode(), data.optString("ownerid"));
                                Log.d("TYPE", "BOOK");

                    } else {
                        Log.d("TYPE", "ELSE STATEMENT FOR SOME REASON");

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

    //Swaps listing list fragment with the opened listing fragment, depending on the value of isBid
    public void openListing(Listing list) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("listingFacade", list);

        if(!list.getIsBid()) {
            listing_opened listing_opened = new listing_opened();
            listing_opened.setArguments(bundle);

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, listing_opened, "listingId").addToBackStack(null);
            fragmentTransaction.commit();

        } else if(list.getIsBid()) {
            listing_opened_bid listing_opened_bid = new listing_opened_bid();
            listing_opened_bid.setArguments(bundle);

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, listing_opened_bid, "listingId").addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    // Method to create the ui bubble after entering a filter in the searchbar
    public void Addbubble(String query) {
        LinearLayout filt_cont  = (LinearLayout) getView().findViewById(R.id.filt_bubble_cont);
        View bubble = getLayoutInflater().inflate(R.layout.fiter_tag_bubble, filt_cont, false);
        TextView bubble_text = (TextView) bubble.findViewById(R.id.bubble_text);
        bubble_text.setText(query);
        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence bubText = bubble_text.getText();
                String parts[] = bubText.toString().split(":");
                Log.d("bubble", "removed filter " + bubble_text.getText() );
                if(parts[0].equals("location")){
                    filtDict.get(parts[0]).remove(0);
                } else{
                    filtDict.get(parts[0]).remove(parts[1]);
                }
                Log.d("filter", String.valueOf(filtDict));
                filt_cont.removeView(v);
                filter();
            }
        });
        filt_cont.addView(bubble);
        Log.d("bubble","Bubble added");
    }

    //Function to filter listings
     public void filter() {
        //Log.d("filter", "Filter column: " + filtCol + " filter content: " + filtContent);
        pushDictionary(filtDict);

    }


    //Pushes dictionary to the server, and changes the contents of the recycler view
    public void pushDictionary(Map<String, ArrayList<String>> filtDict){
        Log.d("filter", "this triggers: "+String.valueOf(filtDict));
        Call<ArrayList<ListFacade>> pushDict = apiAccess.getFilteredInfo(getResources().getString(R.string.apiDevKey), filtDict);
        pushDict.enqueue(new Callback<ArrayList<ListFacade>>() {
            @Override
            public void onResponse(Call<ArrayList<ListFacade>> call, Response<ArrayList<ListFacade>> response) {
                if(!response.isSuccessful()){
                    return;
                }
                list = response.body();

                //To be removed?
                if(!filtDict.get("location").isEmpty()){
                    Log.d("filter", "????");
                    ArrayList<ListFacade> toRemove = new ArrayList<>();
                    for(ListFacade item:list){
                        Location loc;
                        Log.d("filter", "before if item.location == null");
                        if(item.getLocation() == null){
                            Log.d("filter", "item location: " + String.valueOf(item.getLocation()));

                            toRemove.add(item);
                            Log.d("filter", "Kek empty thus removed");
                        } else {
                            Log.d("filter", "location elseer ");
                            loc = new Location("");
                            String[] coords = item.getLocation().split(";");
                            loc.setLatitude(Double.valueOf(coords[0]));
                            loc.setLongitude(Double.valueOf(coords[1]));
                            Log.d("filter", "user location, lat: " + String.valueOf(userLocation.getLatitude()) + "long: " + userLocation.getLongitude() );
                            float[] distance = new float[2];
                            Location.distanceBetween(loc.getLatitude(), loc.getLongitude(), userLocation.getLatitude(), userLocation.getLongitude(), distance);
                            Log.d("filter", "distance between: " + String.valueOf(distance[0]));
                            if(distance[0] > 5000){
                                toRemove.add(item);
                            }
                        }
                    }
                    list.removeAll(toRemove);
                }
                recycleOfferAdapter = new RecycleOfferAdapter(getActivity(), list, selectListener);
                recycler.setAdapter(recycleOfferAdapter);

            }

            @Override
            public void onFailure(Call<ArrayList<ListFacade>> call, Throwable t) {

            }
        });
    }


    @SuppressLint("MissingPermission")
    private Location getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //Location location = new Location("location");
        //Toast.makeText(this, "Location Received", Toast.LENGTH_SHORT).show();
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
                            Log.d("filter", "userLocation: " + String.valueOf(userLocation));
                            //Toast.makeText(AddListingActivity.this, "Location: " + location, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), getAddress(latitude, longitude), Toast.LENGTH_LONG).show();
                            if(!filtDict.get("location").contains(latitude + ";" + longitude)){
                                filtDict.get("location").add(latitude + ";" + longitude);
                                Log.d("filter", "dict after location filt: " + String.valueOf(filtDict));
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
        //Toast.makeText(this, "Return" + location, Toast.LENGTH_SHORT).show();
        return location;
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



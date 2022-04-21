package com.example.messagingapp.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.ApiAccess;
import com.example.messagingapp.R;
import com.example.messagingapp.SelectListener;
import com.example.messagingapp.adapters.RecycleOfferAdapter;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.example.messagingapp.utilities.LocationHandler;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
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

    //Listener interface
    SelectListener selectListener;

    //Retrofit interface
    ApiAccess apiAccess;

    public listing_list() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        filtDict.put("type", new ArrayList<String>());
        filtDict.put("university", new ArrayList<String>());
        filtDict.put("course_code", new ArrayList<String>());
        filtDict.put("isbn", new ArrayList<String>());
        filtDict.put("location", new ArrayList<String>());
        Log.d("filter", String.valueOf(filtDict));


        //Set Title and add filters, depending on how listing list was started
        if (getArguments() != null) {

            Log.d("bundle", String.valueOf(getArguments()));
            personalType = getArguments().getString("title");
            String[] parts = personalType.split(":");
            userId = parts[1];

            titleView.setText(String.valueOf(parts[0] + "'s Offers"));

            //If listing list is started from profile, or from opened listing:
            if (!userId.toString().equals("no")) {
                titleView.setText(String.valueOf(parts[0] + "'s Offers"));
                filtDict.get("author").add(userId);
                Log.d("filter", String.valueOf(filtDict));
                filter();

                //If listing list is stated from nav bar:
            } else {
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
                        if (i == EditorInfo.IME_ACTION_SEARCH) {

                            filtContent = textView.getText().toString().trim();
                            Log.d("filter", "kur");
                            if (!filtContent.isEmpty()) {
                                if (!filtDict.get(filtCol).contains(filtContent) || !filtDict.get("location").isEmpty()) {
                                    Addbubble(filtCol + ":" + filtContent);
                                    filtDict.get(filtCol).add(filtContent);
                                    filter();
                                } else {
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
        if (userId.toString().equals("no")) {
            filter();
        }

        //Add more listings when the user scrolls down
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    count++;
                    progressBar.setVisibility(View.VISIBLE);
                    if (count < 100) {
                        filter();
                    }
                }
            }
        });

    }

    /**
     * Method that initiates View and variables
     *
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
            }
        });
    }

    /**
     * Pushes filter dictionary to the server, then pulls listings based on the filters and
     * refreshes the current listings on screen
     *
     * @param filtDict - contains the filters
     */
    public void pushDictionary(Map<String, ArrayList<String>> filtDict) {
        Log.d("filter", "this triggers: " + String.valueOf(filtDict));
        Call<ArrayList<ListFacade>> pushDict = apiAccess.getFilteredInfo(getResources().getString(R.string.apiDevKey), filtDict);
        pushDict.enqueue(new Callback<ArrayList<ListFacade>>() {
            @Override
            public void onResponse(Call<ArrayList<ListFacade>> call, Response<ArrayList<ListFacade>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                //Pushing the result of the pull request to the listFacade array list
                list = response.body();

                //Filtering by location by comparing current user and item locations
                if (!filtDict.get("location").isEmpty()) {
                    ArrayList<ListFacade> toRemove = new ArrayList<>();
                    for (ListFacade item : list) {
                        Location loc;
                        if (item.getLocation() == null) {
                            toRemove.add(item);
                        } else {
                            loc = LocationHandler.fromString(item.getLocation());
                            float[] distance = new float[2];

                            //Calculating distance between user and item location in meters
                            Location.distanceBetween(loc.getLatitude(), loc.getLongitude(),
                                    userLocation.getLatitude(), userLocation.getLongitude(),
                                    distance);
                            if (distance[0] > 5000) {
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
    private void getLocation() {
        try {
            LocationHandler.getLocation(getContext(), getActivity(), new LocationHandler.onLocationListener() {
                @Override
                public void onLocation(Location location) {
                    userLocation = location;
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Toast.makeText(getContext(), LocationHandler.getAddress(getContext(), location), Toast.LENGTH_LONG).show();
                    if (!filtDict.get("location").contains(latitude + ";" + longitude)) {
                        filtDict.get("location").add(latitude + ";" + longitude);
                    }
                    Addbubble("location:Within 5km");
                    filter();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * On click listener for the RecyclerView rows.Swaps fragments when a row is clicked.
     * Requests the full listing data before opening the fragment
     *
     * @param listFacade
     */
    public void rowOnClick(ListFacade listFacade) {
        Call<ResponseBody> getFullData = apiAccess.getDetailedListing(listFacade.getList_iD(),
                getResources().getString(R.string.apiDevKey));
        getFullData.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
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
                                data.optBoolean("sold"), listFacade.getTitle(), listFacade.getIsbn(), loc,
                                data.optString("lang"), data.optString("aucid"), data.optString("description"), listFacade.getUniversity(),
                                listFacade.getCourseCode(), data.optString("ownerid"));

                    } else {
                        list = new Listing(listFacade.getList_iD(), photos, listFacade.getPrice(), listFacade.getType(), data.optInt("reports"),
                                data.optBoolean("sold"), listFacade.getTitle(), loc,
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
     *
     * @param list - Listing object passed as a parameter to the corresponding fragment
     * @post A new fragment is opened, with data taken from the list parameter
     */
    public void openListing(Listing list) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("listingFacade", list);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!list.getIsBid()) {
            listing_opened listing_opened = new listing_opened();
            listing_opened.setArguments(bundle);

            fragmentTransaction.replace(R.id.frame_layout, listing_opened, "listingId").addToBackStack(null);
            fragmentTransaction.commit();

        } else if (list.getIsBid()) {
            Toast.makeText(getContext(), "This feature is not supported", Toast.LENGTH_LONG);
        }

    }

    /**
     * Method to create the ui bubble after entering a filter in the searchbar
     *
     * @param query- string representing the text input from the AutoCompleteTextView
     */
    public void Addbubble(String query) {
        LinearLayout filt_cont = (LinearLayout) getView().findViewById(R.id.filt_bubble_cont);
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
                if (parts[0].equals("location")) {
                    filtDict.get(parts[0]).remove(0);
                } else {
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
}



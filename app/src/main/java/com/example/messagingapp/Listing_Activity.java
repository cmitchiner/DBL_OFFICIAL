package com.example.messagingapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Listing_Activity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, AdapterView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    int count = 0;
    private final SelectListener selectListener = new SelectListener() {
        @Override
        public void onItemClicked(ListFacade listFacade) {
            recItemClicked(listFacade);
        }
    };
    private ArrayList<String> filtArray = new ArrayList<>();
    ArrayList<ListFacade> list = new ArrayList<>();
    RecyclerView recycler;
    RecycleOfferAdapter recycleOfferAdapter;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    ImageButton addListingButton;
    Spinner spinner;
    AutoCompleteTextView filterText;
    String filtCol;
    String filtContent;

    ApiAccess apiAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_listing_list);
        //initializing variables
        recycler = findViewById(R.id.offerContainer);
        progressBar = findViewById(R.id.idPBLoading);
        nestedScrollView = findViewById(R.id.recScrollView);
        addListingButton = findViewById(R.id.add_offer_butt);
        spinner = findViewById(R.id.filterCol);
        filterText = findViewById(R.id.filterInput);

        //Creating Spinner for filter column selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this
                , R.array.filterColumns, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Making the drop down menu show up on text field click
        /*filterText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                filterText.showDropDown();
                return false;
            }
        });

         */

        //Setting up text view for filtering

        //Adding event listner for soft input
        filterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    filtContent = textView.getText().toString().trim();
                    filterText.clearFocus();
                    Log.d("filter", "kur");
                    if(!filtContent.isEmpty()) {
                        Addbubble(filtContent);
                        filter();
                    }
                    return true;
                }
                return false;
            }
        });

        //adding event listner for hardware keyboard
        filterText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER) {
                    filtContent = filterText.getText().toString().trim();
                    filterText.clearFocus();
                    if(!filtContent.isEmpty()) {
                        Addbubble(filtContent);
                        filter();
                    }
                    return true;
                }
                return false;
            }
        });

        //Setting Add Listing button
        addListingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddListingActivity.class));
            }
        });

        //initializing arrays
        getData();

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(manager);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    count++;
                    progressBar.setVisibility(View.VISIBLE);
                    if(count < 100){
                        getData();
                    }
                }
            }
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);




    }
    private void getData(){
        int rowNum = 55;

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.apiBaseUrl)).addConverterFactory(GsonConverterFactory.create()).build();
        apiAccess = retrofit.create(ApiAccess.class);
        Call<ArrayList<ListFacade>> listingQuery = apiAccess.getInfo(getResources().getString(R.string.apiDevKey), rowNum);
        listingQuery.enqueue(new Callback<ArrayList<ListFacade>>() {
            @Override
            public void onResponse(Call<ArrayList<ListFacade>> call, Response<ArrayList<ListFacade>> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                list = response.body();
                recycleOfferAdapter = new RecycleOfferAdapter(getApplicationContext(), list, selectListener);
                recycler.setAdapter(recycleOfferAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<ListFacade>> call, Throwable t) {
                Log.d(null, t.getMessage());
            }
        });
    }


    //On click listner for the rows
    public void recItemClicked(ListFacade listFacade) {
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
                    JSONArray array = data.optJSONArray("photos");
                    if(array != null) {
                        for(int i = 0; i < array.length(); i++) {
                            photos.add(array.getString(i));
                        }
                    }


                    Location loc = new Location("");
                    String[] coords = listFacade.getLocation().split(";");
                    loc.setLatitude(Double.valueOf(coords[0]));
                    loc.setLongitude(Double.valueOf(coords[1]));

                    Listing list = new Listing(photos, listFacade.getPrice(), listFacade.getType(), data.optInt("reports"),
                            data.optBoolean("sold"), listFacade.getTitle(),listFacade.getIsbn(),loc,
                            data.optString("lang"), data.optString("aucid"), data.optString("description"), listFacade.getUniversity(),
                            listFacade.getCourseCode(), data.optString("ownerid"));
                    openListing(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                return;
            }
        });
    }

    //Swaps fragment with the correpsonding fragment, depending on the value of isBid
    public void openListing(Listing list) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("listingFacade", list);


        if(!list.getIsBid()){
            listing_opened listing_opened = new listing_opened();
            listing_opened.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.replace(R.id.fragChangeCont, listing_opened, "listingId").addToBackStack(null);
            fragmentTransaction.commit();

        } else if(list.getIsBid()) {
            com.example.messagingapp.listing_opened_bid listing_opened_bid = new com.example.messagingapp.listing_opened_bid();
            listing_opened_bid.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.replace(R.id.fragChangeCont, listing_opened_bid, "listingId").addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    //On Item selected events for spinner. TODO set suggested text
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        filtCol = (adapterView.getItemAtPosition(i).toString());
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

    public void Addbubble(String query) {
        LinearLayout filt_cont  = (LinearLayout) findViewById(R.id.filt_bubble_cont);
        View bubble = getLayoutInflater().inflate(R.layout.fiter_tag_bubble, filt_cont, false);
        TextView bubble_text = (TextView) bubble.findViewById(R.id.bubble_text);
        bubble_text.setText(query);
        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence bubText = bubble_text.getText();
                Log.d("bubble", "removed filter " + bubble_text.getText() );
                filt_cont.removeView(v);
            }
        });
        filt_cont.addView(bubble);
        Log.d("bubble","Bubble added");
    }

    //Function to filter listings
    public void filter() {
        Log.d("filter", "Filter column: " + filtCol + " filter content: " + filtContent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.home:
                return true;
            case R.id.profileNavBar:
                startActivity(new Intent(this,ProfileActivity.class));
                return true;
            case R.id.messages:
                startActivity(new Intent(this, MessagesActivity.class));
                return true;

        }
        return false;

    }


}
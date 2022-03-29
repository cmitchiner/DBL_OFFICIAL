package com.example.messagingapp.activities;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.messagingapp.adapters.RecycleOfferAdapter;
import com.example.messagingapp.SelectListener;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
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
 * Use the {@link listing_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listing_list extends Fragment implements AdapterView.OnItemSelectedListener {
    int count = 0;
    ArrayList<ListFacade> list = new ArrayList<>();
    RecyclerView recycler;
    RecycleOfferAdapter recycleOfferAdapter;
    ProgressBar progressBar;
    NestedScrollView nested_scroll;
    ImageButton addListingButton;
    Spinner spinner;
    AutoCompleteTextView filterText;
    String filtCol;
    String filtContent;
    SelectListener selectListener;
    TextView titleView;
    String personalType;
    String userId;
    Map<String, String> filtDict;
    Button locationbutt;

    ApiAccess apiAccess;

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


        //getting the Views for every view component
        initViewsAndVars(view);
        //Set Title and add filter, depending on how listing list was started
        Log.d("filter", String.valueOf(getArguments()));
        if(getArguments() != null){
            Log.d("bundle", String.valueOf(getArguments()));
            personalType = getArguments().getString("title");
            String[] parts = personalType.split(":");
            titleView.setText(String.valueOf(parts[0]));
            userId = parts[1];
            if(!userId.toString().equals("no")){
                filtDict.put("author", userId);
            }
            filtDict.put("author", "");
            filtDict.put("title", "");
            filtDict.put("type", "");
            filtDict.put("university", "");
            filtDict.put("course_code", "");
            filtDict.put("isbn", "");
            filtDict.put("location", "");
            Log.d("filter", String.valueOf(filtDict));
        }


        //Creating Spinner for filter column selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity()
                , R.array.filterColumns, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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
                    filterText.clearFocus();
                    Log.d("filter", "kur");
                    if(!filtContent.isEmpty()) {
                        Addbubble(filtCol+":"+filtContent);
                        filtDict.put(filtCol, filtContent);
                        Log.d("filter", String.valueOf(filtDict));
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
                if (MainActivity.isGuest) {
                    //Deny add listing capability to guests
                    Toast.makeText(getActivity(), "This feature is not allowed for guests", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), AddListingActivity.class));
                }
            }
        });

        //initializing arrays
        getData();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        nested_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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

            }
        });
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
                recycleOfferAdapter = new RecycleOfferAdapter(getActivity(), list, selectListener);
                recycler.setAdapter(recycleOfferAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<ListFacade>> call, Throwable t) {
                Log.d(null, t.getMessage());
            }
        });
    }


    //On click listner for the rows
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

                    Listing list;

                    if (listFacade.getType() == "book") {
                         list = new Listing(photos, listFacade.getPrice(), listFacade.getType(), data.optInt("reports"),
                                data.optBoolean("sold"), listFacade.getTitle(),listFacade.getIsbn(),loc,
                                data.optString("lang"), data.optString("aucid"), data.optString("description"), listFacade.getUniversity(),
                                listFacade.getCourseCode(), data.optString("ownerid"));
                    } else {
                         list = new Listing(photos, listFacade.getPrice(), listFacade.getType(), data.optInt("reports"),
                                data.optBoolean("sold"), listFacade.getTitle(),loc,
                                data.optString("lang"), data.optString("aucid"), data.optString("description"), listFacade.getUniversity(),
                                listFacade.getCourseCode(), data.optString("ownerid"));
                    }

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

    //On Item selected events for spinner. TODO set suggested text
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        filtCol = (adapterView.getItemAtPosition(i).toString());
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
                filtDict.remove(parts[0], parts[1]);
                Log.d("filter", String.valueOf(filtDict));
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
}


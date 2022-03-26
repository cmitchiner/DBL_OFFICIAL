package com.example.messagingapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.messagingapp.ApiAccess;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

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
public class listing_list extends Fragment implements SelectListener, AdapterView.OnItemSelectedListener {
    int count = 0;
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


        //initializing variables
        recycler = view.findViewById(R.id.offerContainer);
        progressBar = view.findViewById(R.id.idPBLoading);
        nestedScrollView = view.findViewById(R.id.nested_scroll);
        addListingButton = view.findViewById(R.id.add_offer_butt);
        spinner = view.findViewById(R.id.filterCol);
        filterText = view.findViewById(R.id.filterInput);

        //Creating Spinner for filter column selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity()
                , R.array.filterColumns, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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
        filterText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        filterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

        //Adding event listner for software keyboard
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    filtContent = textView.getText().toString().trim();
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
                    if(!filtContent.isEmpty()) {
                        Addbubble(filtContent);
                        filter();
                    }
                    return true;
                }
                return false;
            }
        });


        /*
        searchView = view.findViewById(R.id.offer_search);
        searchView.setEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtArray.add(query);
                LinearLayout filt_cont  = (LinearLayout) getView().findViewById(R.id.filt_bubble_cont);
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
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

         */
        //Setting Add Listing button
        addListingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddListingActivity.class));
            }
        });

        //initializing arrays
        getData();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
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
                recycleOfferAdapter = new RecycleOfferAdapter(getActivity(), list, listing_list.this::onItemClicked);
                recycler.setAdapter(recycleOfferAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<ListFacade>> call, Throwable t) {
                Log.d(null, t.getMessage());
            }
        });
    }


    //On click listner for the rows
    @Override
    public void onItemClicked(ListFacade listFacade) {
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

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, listing_opened, "listingId").addToBackStack(null);
            fragmentTransaction.commit();

        } else if(list.getIsBid()) {
            com.example.messagingapp.listing_opened_bid listing_opened_bid = new com.example.messagingapp.listing_opened_bid();
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

    public void Addbubble(String query){
        LinearLayout filt_cont  = (LinearLayout) getView().findViewById(R.id.filt_bubble_cont);
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
    public void filter(){
        Log.d("filter", "Filter column: " + filtCol + " filter content: " + filtContent);
    }
}


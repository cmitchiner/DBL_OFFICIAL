package com.example.messagingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;

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
public class listing_list extends Fragment implements SelectListener{
    int count = 0;
    private ArrayList<String> filtArray = new ArrayList<>();
    private ArrayList<View> rowsArray = new ArrayList<>();
    ArrayList<ListFacade> list = new ArrayList<>();
    RecyclerView recycler;
    com.example.messagingapp.RecycleOfferAdapter recycleOfferAdapter;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    Thread offerthread;

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
                    if(count < 10){
                        getData();
                    }
                }
            }
        });

    }
    private void getData(){
        int rowNum = 55;

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.apiBaseUrl)).addConverterFactory(GsonConverterFactory.create()).build();
        ApiAccessB apiAccessB = retrofit.create(ApiAccessB.class);
        Call<ArrayList<ListFacade>> listings = apiAccessB.getInfo(getResources().getString(R.string.apiDevKey), rowNum);
        listings.enqueue(new Callback<ArrayList<ListFacade>>() {
            @Override
            public void onResponse(Call<ArrayList<ListFacade>> call, Response<ArrayList<ListFacade>> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                list = response.body();
                // on below line we are adding our array list to our adapter class.
                recycleOfferAdapter = new com.example.messagingapp.RecycleOfferAdapter(getActivity(), list, listing_list.this::onItemClicked);

                // on below line we are setting
                // adapter to our recycler view.
                recycler.setAdapter(recycleOfferAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<ListFacade>> call, Throwable t) {

            }
        });



    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.searchbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type to filter");
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
                        recycleOfferAdapter.getFilter().filter(bubble_text.getText());
                        Log.d("bubble", "removed filter " + bubble_text.getText() );
                        filt_cont.removeView(v);
                    }
                });
                filt_cont.addView(bubble);
                recycleOfferAdapter.getFilter().filter(query);
                Log.d("bubble","Bubble added");
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onItemClicked(ListFacade listFacade) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("listingFacade", listFacade);
        Log.d("isbn", String.valueOf(listFacade.getIsbn()));
        Log.d("isbn", listFacade.getTitle() + " " + listFacade.getUniversity() + " " + listFacade.getPrice() );

        if(listFacade.getIsBid()){
            listing_opened listing_opened = new listing_opened();
            listing_opened.setArguments(bundle);

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nested_scroll, listing_opened, "listingId");
            fragmentTransaction.commit();

        } else if(!listFacade.getIsBid()){
            com.example.messagingapp.listing_opened_bid listing_opened_bid = new com.example.messagingapp.listing_opened_bid();
            listing_opened_bid.setArguments(bundle);

            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nested_scroll, listing_opened_bid, "listingId");
            fragmentTransaction.commit();
        }
    }
}


package com.example.messagingapp;

import static java.util.Objects.isNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link listing_opened_bid#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listing_opened_bid extends Fragment {
    ListFacade listFacade;
    Listing listing;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public listing_opened_bid() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment listing_opened_bid.
     */
    // TODO: Rename and change types and number of parameters
    public static listing_opened_bid newInstance(String param1, String param2) {
        listing_opened_bid fragment = new listing_opened_bid();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listing_opened_bid, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listFacade = getArguments().getParcelable("listingFacade");

        ImageView image = (ImageView) view.findViewById(R.id.list_image);
        TextView title = (TextView) view.findViewById(R.id.list_title);
        TextView author = (TextView) view.findViewById(R.id.list_author);
        TextView description = (TextView) view.findViewById(R.id.list_desc);
        TextView university = (TextView) view.findViewById(R.id.list_university);
        TextView courseCode = (TextView) view.findViewById(R.id.list_code);
        TextView price = (TextView) view.findViewById(R.id.list_price);
        TextView rating = (TextView) view.findViewById(R.id.list_rating);
        TextView isbn  = (TextView) view.findViewById(R.id.isbn);


        //image.setImageDrawable();
        title.setText(listFacade.getTitle());
        //author.setText(listFacade.getAuthor());
        //description.setText(listing.getDescription());
        university.setText(listFacade.getUniversity());
        courseCode.setText(listFacade.getCourseCode());
        price.setText(listFacade.getPrice()/100 + "€");
        //rating.setText(getString(listing.getRating()));
        /*if(!isNull(listFacade.getIsbn())){
            isbn.setVisibility(View.VISIBLE);
            isbn.setText(String.valueOf(listFacade.getIsbn()));
        } else{
            isbn.setVisibility(View.INVISIBLE);
        }

         */
    }

    public void getData(){

    }
}
package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messagingapp.R;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.example.messagingapp.objects.User;
import com.google.firebase.auth.FirebaseAuth;
//import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link listing_opened#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listing_opened extends Fragment {
    ListFacade listFacade;
    Listing listing;
    String currentUserId;
    String authorId;
    Button completeListing;
    Button messageButton;
    String usernameAuthor;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public listing_opened() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment listing_opened.
     */
    // TODO: Rename and change types and number of parameters
    public static listing_opened newInstance(String param1, String param2) {
        listing_opened fragment = new listing_opened();
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
        return inflater.inflate(R.layout.fragment_listing_opened, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listing = getArguments().getParcelable("listingFacade");

        ImageView image = (ImageView) view.findViewById(R.id.list_image);
        TextView title = (TextView) view.findViewById(R.id.list_title);
        TextView author = (TextView) view.findViewById(R.id.list_author);
        TextView description = (TextView) view.findViewById(R.id.list_desc);
        TextView university = (TextView) view.findViewById(R.id.list_university);
        TextView courseCode = (TextView) view.findViewById(R.id.list_code);
        TextView price = (TextView) view.findViewById(R.id.list_price);
        TextView rating = (TextView) view.findViewById(R.id.list_rating);
        TextView isbn  = (TextView) view.findViewById(R.id.isbn);
        ImageView backBtn = view.findViewById(R.id.backBtn);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        completeListing = (Button) view.findViewById(R.id.MarkAsComplete);
        messageButton = (Button) view.findViewById(R.id.message_button);

        //image.setImageDrawable();
        title.setText(listing.getTitle());
        author.setText(listing.getUser());
        description.setText(listing.getDescription());
        university.setText(listing.getUniversity());
        courseCode.setText(listing.getCourseCode());
        double priceEuro = listing.getPrice();
        price.setText(String.valueOf( priceEuro/100 + "€"));
        //rating.setText(getString(listing.getRating()));

        if(!String.valueOf(listing.getIsbn()).equals("0")){
            isbn.setVisibility(View.VISIBLE);
            isbn.setText(String.valueOf(listing.getIsbn()));
        } else{
            isbn.setVisibility(View.INVISIBLE);
        }

        if(currentUserId == authorId ){
            completeListing.setVisibility(View.VISIBLE);
        }

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserListingsActivity.class);
                Bundle send = new Bundle();
                String user = usernameAuthor;
                String authorrid = listing.getUser().toString();
                String usidCombo = user+":"+authorrid;
                send.putString(usidCombo, "title");
                intent.putExtras(send);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Listing_Activity.class));
            }
        });

        //insert rating of user here
        ratingBar.setRating(3);

    }

}
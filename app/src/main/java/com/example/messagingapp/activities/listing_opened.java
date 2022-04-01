package com.example.messagingapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messagingapp.ApiAccess;
import com.example.messagingapp.R;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.example.messagingapp.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.example.messagingapp.objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link listing_opened#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listing_opened extends Fragment implements View.OnClickListener {

    /**
     * VARIABLES
     **/
    //Variables to hold listing object, and data parsing class list facade
    private Listing listing;
    private Context context;

    //Declare variables for XML references
    private Button completeListing;
    private ImageButton messageButton;
    private RatingBar ratingBar;
    private TextView title, author, description, university, courseCode, price, isbn;
    private ImageView backBtn;
    private FirebaseDatabase firebaseDatabase;

    //Variables to hold information about listing
    private String usernameAuthor;
    private String currentUserId;
    private String authorId;
    private double priceEuro;

    //Default fragment variables
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /** METHODS **/

    /**
     * DEFAULT CONSTRUCTOR
     **/
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
        //Grab current user, and listing object
        if(!MainActivity.isGuest){
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        listing = getArguments().getParcelable("listingFacade");

        context = view.getContext();

        //init references to XML
        ImageView image = view.findViewById(R.id.list_image);
        title = view.findViewById(R.id.list_title);
        author = view.findViewById(R.id.list_author);
        description = view.findViewById(R.id.list_desc);
        university = view.findViewById(R.id.list_university);
        courseCode = view.findViewById(R.id.list_code);
        price = view.findViewById(R.id.list_price);
        isbn = view.findViewById(R.id.isbn);
        backBtn = view.findViewById(R.id.backBtn);
        ratingBar = view.findViewById(R.id.ratingBar);
        completeListing = view.findViewById(R.id.MarkAsComplete);
        messageButton = view.findViewById(R.id.message_button);

        //Fill text for listings
        title.setText(listing.getTitle());
        description.setText(listing.getDescription());
        university.setText(listing.getUniversity());
        courseCode.setText(listing.getCourseCode());
        priceEuro = listing.getPrice();
        price.setText(String.valueOf(priceEuro / 100 + "€"));
        authorId = listing.getUser();
        //Check if listing has an ISBN or not
        if (!String.valueOf(listing.getIsbn()).equals("0")) {
            isbn.setVisibility(View.VISIBLE);
            isbn.setText(String.valueOf(listing.getIsbn()));
        } else {
            isbn.setVisibility(View.INVISIBLE);
        }
        //Check if current user is the creator of the listing, and show complete button if so
        if(MainActivity.isGuest) {
            completeListing.setVisibility(View.GONE);
        } else{
            if (currentUserId.equals(authorId)) {
                completeListing.setVisibility(View.VISIBLE);
            } else {
                completeListing.setVisibility(View.GONE);
            }
        }
        if(listing.getPhotos().get(0) != null) {
            String url = getResources().getString(R.string.apiBaseUrl)+"img/"+listing.getPhotos().get(0)+"?"+getResources().getString(R.string.apiDevKey);
            Log.d("URL", url);
            Picasso.get().load(getResources().getString(R.string.apiBaseUrl)+"img/"+listing.getPhotos().get(0)+"?apiKey="+getResources().getString(R.string.apiDevKey)).into(image);
        }

        //Setup on click listeners
        messageButton.setOnClickListener(this);
        author.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        completeListing.setOnClickListener(this);

        //insert rating of user here
        firebaseDatabase = FirebaseDatabase.getInstance("https://justudy-ebc7b-default-rtdb.europe-west1" +
                ".firebasedatabase.app/");
        fillRatingField();

    }

    private void fillRatingField() {
        DatabaseReference ref = firebaseDatabase.getReference("Users").child(listing.getUser());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                ratingBar.setRating(user.rating);
                author.setText(user.fullName);
                usernameAuthor = user.username;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Metoo", "failed to read user rating");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_button:
                if (!MainActivity.isGuest) {
                    String authIdCombo;
                    authIdCombo = usernameAuthor + ":" + listing.getUser().toString();
                    Intent intent = new Intent(getActivity(), NewMessageActivity.class);
                    intent.putExtra("contact", authIdCombo);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Guests cannot use this feature!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.list_author:
                String user = usernameAuthor;
                String authorrid = listing.getUser().toString();
                String usidCombo = user + ":" + authorrid;
                Log.d("filter", usidCombo);
                Intent intent = new Intent(getActivity(), UserListingsActivity.class);
                intent.putExtra("title", usidCombo);
                startActivity(intent);
                break;
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
            case R.id.MarkAsComplete:
                //Prompt user for username of who they sold too
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt_sold_listing, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        checkUserExists(userInput.getText().toString());
                                        Log.d("COMPLETED_LISTING", userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                break;
        }
    }

    /**
     * checkUserExists(): checks if a given username exists and if found calls startChatWithUser()
     *
     * @param userToCheck the username the current user wants to chat with
     */
    private void checkUserExists(String userToCheck) {
        //References the users branch on our database
        DatabaseReference ref = firebaseDatabase.getReference("Users");

        //Attempt to find a User Class in database with passed String receiverUsername
        ref.orderByChild("username").equalTo(userToCheck).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) { //Receiving user found
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                User user = snapshot1.getValue(User.class);
                                //snapshot1.getKey() contains the receiving users UID
                                markListingAsSold();
                                Toast.makeText(getActivity(),
                                        "Listing marked as sold to: " + user.fullName,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else { //Receiving user not found, alert current user
                            Toast.makeText(getActivity(),
                                    "This user does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //Database request was canceled.
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("REGISTER", error.getMessage());
                    }
                });
    }

    private void markListingAsSold() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.apiBaseUrl)).addConverterFactory(GsonConverterFactory.create()).build();
        ApiAccess apiAccess = retrofit.create(ApiAccess.class);
        JSONObject json = new JSONObject();
        try {
            json.put("list_id", listing.getListId());
            json.put("sold", true);
        } catch (JSONException e) {

        }
        Call<ResponseBody> call = apiAccess.updateListing(json);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}







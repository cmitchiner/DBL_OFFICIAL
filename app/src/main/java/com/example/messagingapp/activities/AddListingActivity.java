package com.example.messagingapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.messagingapp.ApiAccess;
import com.example.messagingapp.R;
import com.example.messagingapp.model.Listing;
import com.example.messagingapp.utilities.LocationHandler;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Activity for creating and adding a listing to the app
 */
public class AddListingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText edtTxtTitle, edtTxtDescription, edtTxtCourseCode, edtTxtCourseName, edtTxtPrice, edtTxtISBN;
    private TextView textview, txtPrice, warningTitle, warningCourseCode, warningCourseName, warningUniversity, warningDescription, warningISBN, warningPicture;
    private Button btnPublish;
    private ImageButton btnUploadPicture;
    private ImageView imgView, imgViewLocation;
    private RadioButton rbBidding, rbSetPrice, rbNotes, rbSummary, rbBook;
    private RelativeLayout parent;
    private ConstraintLayout ActivityProfileLayout;
    private ArrayList<String> arrayList;
    private Dialog dialog;
    private TextInputLayout txtISBN;
    private boolean ISBN = false, bidding = false, pictureUploaded = false;
    private Button setLocationButt;
    private String type = "Notes";
    private File image;
    String locString;

    Retrofit retrofit;
    ApiAccess apiAccess;


    /**
     * onCreate() is a method that runs before a user see's the current activity
     *
     * @param savedInstanceState the previous state of the app to be loaded
     * @post All variables are initialized and Auth Tokens are setup correctly
     * @returns void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);
        //Init references to activity_add_listing.xml
        initViews();

        //Init On-Click Listeners
        btnPublish.setOnClickListener(this);
        btnUploadPicture.setOnClickListener(this);
        rbBidding.setOnClickListener(this);
        rbSetPrice.setOnClickListener(this);
        textview.setOnClickListener(this);
        rbNotes.setOnClickListener(this);
        rbSummary.setOnClickListener(this);
        rbBook.setOnClickListener(this);
        setLocationButt.setOnClickListener(this);

    }

    /**
     * onClick(): holds all the On-Click listeners for any elements on the screen
     *
     * @param v the view that gets clicked on
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Publish the listing
            case R.id.btnPublish:
                if (validateData()) {
                    new Thread(new Runnable() {
                        // New thread to upload data in the background instead of on the ui thread
                        @Override
                        public void run() {
                            initPublish();
                        }
                    }).start();
                } else {
                    Toast.makeText(this, "Not all required fields are filled in", Toast.LENGTH_SHORT).show();
                }
                break;
            //Get an image from the camera or gallery
            case R.id.btnUploadPicture:
                selectImage();
                break;
            // If selling style is set to bidding
            case R.id.rbBidding:
                txtPrice.setText("Starting price: ");
                bidding = true;
                break;
            // If selling style is set to a set price
            case R.id.rbSetPrice:
                txtPrice.setText("Set price: ");
                bidding = false;
                break;
            // If the textview for filling in the university is pressed
            case R.id.testView:
                searchableSpinner();
                break;
            // Make sure the user can fill in an ISBN, when 'Book' is selected
            case R.id.rbBook:
                txtISBN.setVisibility(View.VISIBLE);
                ISBN = true;
                type = "Book";
                break;
            // When the user selects notes the isbn shouldn't be shown
            case R.id.rbNotes:
                txtISBN.setVisibility(View.GONE);
                warningISBN.setVisibility(View.GONE);
                ISBN = false;
                type = "Notes";
                break;
            // When the user selects summary the isbn should also not be shown
            case R.id.rbSummary:
                txtISBN.setVisibility(View.GONE);
                warningISBN.setVisibility(View.GONE);
                ISBN = false;
                type = "Summary";
                break;
            // When add location is pressed call the getLocation function
            case R.id.addLocationListButt:
                getLocation();
                break;
        }
    }

    /**
     * Creates a dialog of a spinner with a searchbar for selecting a university
     */
    private void searchableSpinner() {
        dialog = new Dialog(AddListingActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(1000, 1500);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddListingActivity.this, R.layout.simple_list_item, arrayList);
        listView.setAdapter(adapter);
        // Code for the searchbar
        editText.addTextChangedListener(new TextWatcher() {
            // We ignore this callback as we don't need it
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            // We do need this callback as we only need to do something when the text is changed
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            // We ignore this callback as we don't need it
            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // when item selected from list
                // set selected item on textView
                textview.setText(adapter.getItem(position));

                // Dismiss dialog
                dialog.dismiss();
            }
        });

    }

    /**
     * Allows the user to either take a photo or select a photo from their gallery
     */
    private void selectImage() {
        final CharSequence[] options = {"Take photo", "Choose image from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddListingActivity.this);
        builder.setTitle("Add an image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //allows the user to cancel selecting a picture
                if (options[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                //allows the user to take a photo on the spot
                } else if (options[i].equals("Take photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                //allows the user to choose an image from their gallery
                } else if (options[i].equals("Choose image from gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            }
        });
        builder.show();
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (requestCode == 2) {
                // Image chosen from gallery
                Uri selectedImage = data.getData();
                imgView.setVisibility(View.VISIBLE);
                imgView.setImageURI(selectedImage);
                image = new File(selectedImage.getPath());
            } else if (requestCode == 1) {
                // Image taken while using app
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        image = new File(getApplicationContext().getCacheDir(), "image");
                        try {
                            //create a file to write bitmap data
                            File f = new File(getApplicationContext().getCacheDir(), "image");
                            f.createNewFile();

                            //Convert bitmap to byte array
                            Bitmap bitmap = imageBitmap;
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            //write the bytes in file
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(f);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            try {
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            image = f;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                pictureUploaded = true;
                imgView.setVisibility(View.VISIBLE);
                imgView.setImageBitmap(imageBitmap);
            }
        }
    }

    /**
     * Sending the listing to the database when the publish button is clicked and if all required fields are filled in
     */
    private void initPublish() {
        Log.d(TAG, "initPublish: started");

        retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.apiBaseUrl)).addConverterFactory(GsonConverterFactory.create()).build();
        apiAccess = retrofit.create(ApiAccess.class);
        if (image != null) {
            RequestBody part = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part img = MultipartBody.Part.createFormData("photo", image.getName(), part);
            uploadImage(img);
        } else {
            ArrayList<String> temp = new ArrayList<>();
            temp.add("PLACEHOLDER");
            uploadListing(temp);
        }
        startActivity(new Intent(this, ProfileActivity.class));

    }


    /**
     * Function that takes a Multipart Image and uploads it to the backend, then invokes upload listing
     *
     * @param image the image to be uploaded
     */
    private void uploadImage(MultipartBody.Part image) {
        Call<ResponseBody> uploadImg = apiAccess.uploadImg(image, getResources().getString(R.string.apiDevKey));
        uploadImg.enqueue(new Callback<ResponseBody>() {
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
                ArrayList<String> photoString = new ArrayList<>();
                JSONArray photos = data.optJSONArray("photos");
                for (int i = 0; i < photos.length(); i++) {
                    photoString.add(photos.optString(i));
                }
                uploadListing(photoString);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    /**
     * Method that uploads a listing to the database
     *
     * @param photoString An arraylist containing the names of the photos to be added
     */
    private void uploadListing(ArrayList<String> photoString) {
        double price = Double.parseDouble(edtTxtPrice.getText().toString()) * 100;
        int priceInt = (int) price;
        Listing listing;
        if (ISBN) {
            long ISBNlong = Long.parseLong(edtTxtISBN.getText().toString());
            listing = new Listing(null, photoString, priceInt, type, 0, false, edtTxtTitle.getText().toString(),
                    ISBNlong, locString, "eng", null, edtTxtDescription.getText().toString(), textview.getText().toString(),
                    edtTxtCourseCode.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else {
            listing = new Listing(null, photoString, priceInt, type, 0, false, edtTxtTitle.getText().toString(), locString,
                    "eng", null, edtTxtDescription.getText().toString(), textview.getText().toString(),
                    edtTxtCourseCode.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        Log.d("adder", "listing location: " + String.valueOf(listing.getlocation()));
        Call<ResponseBody> call2 = apiAccess.addNewListing(listing, getResources().getString(R.string.apiDevKey));
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                Toast.makeText(AddListingActivity.this, "Listing successfully created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    /**
     * Validates that all the required fields are non-empty
     *
     * @returns
     */
    private boolean validateData() {
        boolean i = true;
        if (edtTxtTitle.getText().toString().equals("")) {
            warningTitle.setVisibility(View.VISIBLE);
            i = false;
        } else {
            warningTitle.setVisibility(View.GONE);
        }
        if (textview.getText().toString().equals("Select University")) {
            warningUniversity.setVisibility(View.VISIBLE);
            i = false;
        } else {
            warningUniversity.setVisibility(View.GONE);
        }
        if (edtTxtDescription.getText().toString().equals("")) {
            warningDescription.setVisibility(View.VISIBLE);
            i = false;
        } else {
            warningDescription.setVisibility(View.GONE);
        }
        if (ISBN) {
            if (edtTxtISBN.getText().toString().matches("^[0-9]+$") && edtTxtISBN.getText().toString().matches("^97[8-9].*$") && edtTxtISBN.length() == 13) {
                warningISBN.setVisibility(View.GONE);
            } else {
                warningISBN.setVisibility(View.VISIBLE);
                i = false;
            }
        }
        return i;
    }

    /**
     * obtains the user's location
     */
    private void getLocation() {
        //tries to obtain the location by using the LocationHandler class
        try {
            LocationHandler.getLocation(AddListingActivity.this, this, new LocationHandler.onLocationListener() {
                @Override
                public void onLocation(Location location) {
                    locString = LocationHandler.toString(location);
                    Toast.makeText(AddListingActivity.this, LocationHandler.getAddress(AddListingActivity.this, location), Toast.LENGTH_SHORT).show();
                }
            });
            setLocationButt.setVisibility(View.GONE);
            imgViewLocation.setVisibility(View.VISIBLE);
        //if the LocationHandler gives any error it will tell the user
        } catch (Exception e) {
            Toast.makeText(AddListingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Inits all references to Activity_Add_Listing.xml and pulls the arraylist of university
     * from strings.xml
     */
    private void initViews() {
        edtTxtTitle = findViewById(R.id.edtTxtTitle);
        edtTxtDescription = findViewById(R.id.edtTxtDescription);
        edtTxtCourseCode = findViewById(R.id.edtTxtCourseCode);
        edtTxtCourseName = findViewById(R.id.edtTxtCourseName);
        edtTxtISBN = findViewById(R.id.edtTxtISBN);
        edtTxtPrice = findViewById(R.id.edtTxtPrice);

        btnPublish = findViewById(R.id.btnPublish);
        btnUploadPicture = findViewById(R.id.btnUploadPicture);

        txtPrice = findViewById(R.id.txtPrice);
        warningTitle = findViewById(R.id.warningTitle);
        warningUniversity = findViewById(R.id.warningUniversity);
        warningDescription = findViewById(R.id.warningDescription);
        warningISBN = findViewById(R.id.warningISBN);
        textview = findViewById(R.id.testView);
        txtISBN = findViewById(R.id.txtISBN);

        rbBidding = findViewById(R.id.rbBidding);
        rbSetPrice = findViewById(R.id.rbSetPrice);
        rbNotes = findViewById(R.id.rbNotes);
        rbSummary = findViewById(R.id.rbSummary);
        rbBook = findViewById(R.id.rbBook);

        parent = findViewById(R.id.parent);
        ActivityProfileLayout = findViewById(R.id.ActivityProfileLayout);
        imgView = findViewById(R.id.imgView);

        imgViewLocation = findViewById(R.id.imgViewLocation);

        setLocationButt = findViewById(R.id.addLocationListButt);

        arrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Universities)));
        Collections.sort(arrayList);
    }
}

package com.example.messagingapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.messagingapp.R;
import com.example.messagingapp.model.Listing;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.example.messagingapp.ApiAccess;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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


    /** onCreate() is a method that runs before a user see's the current activity
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
     * @param v a view of all elements present on the screen
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnPublish:
                if (validateData()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initPublish();
                    }
                }).start();} else {
                    Toast.makeText(this, "Not all required fields are filled in", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.btnUploadPicture:
                selectImage();
                break;
            case R.id.rbBidding:
                txtPrice.setText("Starting price: ");
                bidding = true;
                break;
            case R.id.rbSetPrice:
                txtPrice.setText("Set price: ");
                bidding = false;
                break;
            case R.id.testView:
                searchableSpinner();
                break;
            case R.id.rbBook:
                // Make sure the user can fill in an ISBN, when 'Book' is selected
                txtISBN.setVisibility(View.VISIBLE);
                ISBN = true;
                type = "Book";
                break;
            case R.id.rbNotes:
                txtISBN.setVisibility(View.GONE);
                warningISBN.setVisibility(View.GONE);
                ISBN = false;
                type = "Notes";
                break;
            case R.id.rbSummary:
                txtISBN.setVisibility(View.GONE);
                warningISBN.setVisibility(View.GONE);
                ISBN = false;
                type = "Summary";
                break;
            case R.id.addLocationListButt:
                getLocation();
                setLocationButt.setVisibility(View.GONE);
                imgViewLocation.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Creates a dialog of a spinner with a searchbar
     */
    private void searchableSpinner() {
        dialog = new Dialog(AddListingActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(1000,1500);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddListingActivity.this, R.layout.simple_list_item, arrayList);
        listView.setAdapter(adapter);
        // Code for the searchbar
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        private void selectImage() {
            final CharSequence[] options = {"Take photo", "Choose image from gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(AddListingActivity.this);
            builder.setTitle("Add an image");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (options[i].equals("Cancel")) {
                        dialogInterface.dismiss();
                        //TODO: Fix the bug that when Cancel is pressed, the publish button gives
                        // This is a bug that Bartjan had, but I don't seem to have it
                    } else if(options[i].equals("Take photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    } else if (options[i].equals("Choose image from gallery")){
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                }
            });
            builder.show();
        }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data != null) {
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

                Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.apiBaseUrl)).addConverterFactory(GsonConverterFactory.create()).build();
                ApiAccess apiAccess = retrofit.create(ApiAccess.class);
                RequestBody part = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part img = MultipartBody.Part.createFormData("photo", image.getName(), part);
                Call<ResponseBody> uploadImg = apiAccess.uploadImg(img, getResources().getString(R.string.apiDevKey));
                uploadImg.enqueue(new Callback<ResponseBody>() {
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
                        ArrayList<String> photoString = new ArrayList<>();
                        JSONArray photos = data.optJSONArray("photos");
                        for(int i = 0; i < photos.length(); i++){
                            photoString.add(photos.optString(i));
                        }
                        double price = Double.parseDouble(edtTxtPrice.getText().toString()) * 100;
                        int priceInt = (int) price;
                        Listing listing;
                        if (ISBN) {
                            long ISBNlong = Long.parseLong(edtTxtISBN.getText().toString());
                            listing = new Listing(null, photoString, priceInt, type, 0, false, edtTxtTitle.getText().toString(),
                                    ISBNlong, null, "eng", null, edtTxtDescription.getText().toString(), textview.getText().toString(),
                                    edtTxtCourseCode.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }else {
                            listing = new Listing(null, photoString, priceInt, type, 0, false, edtTxtTitle.getText().toString(), null,
                                    "eng", null, edtTxtDescription.getText().toString(), textview.getText().toString(),
                                    edtTxtCourseCode.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }

                        Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.apiBaseUrl)).addConverterFactory(GsonConverterFactory.create()).build();
                        ApiAccess apiAccess = retrofit.create(ApiAccess.class);
                        Call<ResponseBody> call2 = apiAccess.addNewListing(listing, getResources().getString(R.string.apiDevKey));
                        call2.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                showSnackBar();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d(null,  "BIG Fail");
                                t.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(null,  "BIG Fail");
                        t.printStackTrace();


                    }
                });
                startActivity(new Intent(this, ProfileActivity.class));

        }


    /**
     * Shows a snackbar when successfully published listing
     */
    private void showSnackBar() {
            Log.d(TAG, "showSnackBar: started");
            Snackbar.make(ActivityProfileLayout, "Offer added", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
        }

    /**
     * Validates that all the required fields are non-empty
     * @returns
     */
    private boolean validateData() {
        boolean i = true;
            if (edtTxtTitle.getText().toString().equals("")) {
                warningTitle.setVisibility(View.VISIBLE);
                i = false;
            }else {
                warningTitle.setVisibility(View.GONE);
            }
            if (textview.getText().toString().equals("Select University")) {
                warningUniversity.setVisibility(View.VISIBLE);
                i = false;
            }else {
                warningUniversity.setVisibility(View.GONE);
            }
            if (edtTxtCourseCode.getText().toString().equals("")) {
                warningCourseCode.setVisibility(View.VISIBLE);
                i =  false;
            }else {
                warningCourseCode.setVisibility(View.GONE);
            }
            if (edtTxtCourseName.getText().toString().equals("")) {
                warningCourseName.setVisibility(View.VISIBLE);
                i =  false;
            }else {
                warningCourseName.setVisibility(View.GONE);
            }
            if (edtTxtDescription.getText().toString().equals("")) {
                warningDescription.setVisibility(View.VISIBLE);
                i = false;
            }else {
                warningDescription.setVisibility(View.GONE);
            }
            if (ISBN) {
                if(edtTxtISBN.getText().toString().matches("^[0-9]+$") && edtTxtISBN.getText().toString().matches("^97[8-9].*$") && edtTxtISBN.length()==13) {
                    warningISBN.setVisibility(View.GONE);
                }else {
                    warningISBN.setVisibility(View.VISIBLE);
                    i = false;
                }
            }
            if (!pictureUploaded) {
                warningPicture.setVisibility(View.VISIBLE);
                i = false;
            }else {
                warningPicture.setVisibility(View.GONE);
            }
            return i;
        }

    private void getLocation() {
        Toast.makeText(this, "Location Received", Toast.LENGTH_SHORT).show();
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
            warningCourseCode = findViewById(R.id.warningCourseCode);
            warningCourseName = findViewById(R.id.warningCourseName);
            warningUniversity = findViewById(R.id.warningUniversity);
            warningDescription = findViewById(R.id.warningDescription);
            warningISBN = findViewById(R.id.warningISBN);
            warningPicture = findViewById(R.id.warningPicture);
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

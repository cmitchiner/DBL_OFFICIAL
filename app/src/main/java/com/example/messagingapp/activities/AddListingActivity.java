package com.example.messagingapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.messagingapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AddListingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText edtTxtTitle, edtTxtDescription, edtTxtPrice, edtTxtCourseCode, edtTxtCourseName, edtTxtISBN;
    private TextView textview, txtAddOffer, txtCategory, txtDescription, txtUploadDocument, txtUploadPicture, txtPrice, txtEuro, warningTitle, warningCourseCode, warningCourseName, warningUniversity, warningDescription, warningISBN;
    private Spinner spinnerUniversity, spinnerCourseCode;
    private RadioGroup rgCategory, rgBid;
    private Button btnPublish;
    private ImageButton btnUploadPicture, btnUploadDocument;
    private ImageView imgView;
    private RadioButton rbBidding, rbSetPrice, rbNotes, rbSummary, rbBook;
    private RelativeLayout parent;
    private ArrayList<String> arrayList;
    private Dialog dialog;
    private TextInputLayout txtISBN;
    private boolean ISBN = false;
    private Button setLocationButt;


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
        btnUploadDocument.setOnClickListener(this);
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
                initPublish();
                break;
            case R.id.btnUploadPicture:
                selectImage();
                break;
            case R.id.btnUploadDocument:
                chooseFile();
                break;
            case R.id.rbBidding:
                txtPrice.setText("Starting price: ");
                break;
            case R.id.rbSetPrice:
                txtPrice.setText("Set price: ");
                break;
            case R.id.testView:
                searchableSpinner();
                break;
            case R.id.rbBook:
                txtISBN.setVisibility(View.VISIBLE);
                ISBN = true;
                break;
            case R.id.rbNotes:
            case R.id.rbSummary:
                txtISBN.setVisibility(View.GONE);
                ISBN = false;
                break;
            case R.id.locationFiltButt:
                getLocation();
                break;

        }
    }


    private void searchableSpinner() {
        dialog=new Dialog(AddListingActivity.this);
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(1000,1500);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText editText=dialog.findViewById(R.id.edit_text);
        ListView listView=dialog.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(AddListingActivity.this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);
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

        private void chooseFile() {
            //System.out.println(type2);
            Intent intent = new Intent();
            //intent.setType("application/pdf");
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 3 );
        }

        public String getStringPdf (Uri filepath){
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                inputStream =  getContentResolver().openInputStream(filepath);

                byte[] buffer = new byte[1024];
                byteArrayOutputStream = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            byte[] pdfByteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(pdfByteArray, Base64.DEFAULT);
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
            if (requestCode==2) {
                Uri selectedImage = data.getData();
                imgView.setImageURI(selectedImage);
            } else if (requestCode==1) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgView.setImageBitmap(imageBitmap);
            } else if (requestCode==3) {
                Uri filePath = data.getData();
                getStringPdf(filePath);
                Toast.makeText(this, "File chosen", Toast.LENGTH_SHORT).show();
            }
        }

        private void initPublish() {
            Log.d(TAG, "initPublish: started");
            if (validateData()) {
                showSnackBar();
            }else {
                Toast.makeText(this, "Not all required fields are filled in", Toast.LENGTH_SHORT).show();
            }
        }

        private void showSnackBar() {
            Log.d(TAG, "showSnackBar: started");
            Snackbar.make(parent, "Offer added", Snackbar.LENGTH_INDEFINITE)
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
                if(edtTxtISBN.getText().toString().equals("")) {
                    warningISBN.setVisibility(View.VISIBLE);
                    i = false;
                }else {
                    warningISBN.setVisibility(View.GONE);
                }
                if (edtTxtISBN.getText().toString().matches("^[0-9-]+$")) {

                } else {
                    Toast.makeText(this, "ISBN should only contain numbers and dashes", Toast.LENGTH_SHORT).show();
                    i = false;
                }
            }
            return i;
        }

    private void getLocation() {
    }

    /**
     * Inits all references to Activity_Add_Listing.xml and pulls the arraylist of university
     * from strings.xml
     */
    private void initViews() {
            edtTxtTitle = findViewById(R.id.edtTxtTitle);
            edtTxtDescription = findViewById(R.id.edtTxtDescription);
            edtTxtPrice = findViewById(R.id.edtTxtPrice);
            edtTxtCourseCode = findViewById(R.id.edtTxtCourseCode);
            edtTxtCourseName = findViewById(R.id.edtTxtCourseName);
            edtTxtISBN = findViewById(R.id.edtTxtISBN);

            btnPublish = findViewById(R.id.btnPublish);
            btnUploadDocument = findViewById(R.id.btnUploadDocument);
            btnUploadPicture = findViewById(R.id.btnUploadPicture);

            txtAddOffer = findViewById(R.id.txtAddOffer);
            txtCategory = findViewById(R.id.txtCategory);
            txtPrice = findViewById(R.id.txtPrice);
            txtUploadDocument = findViewById(R.id.txtUploadDocument);
            txtUploadPicture = findViewById(R.id.txtUploadPicture);
            warningTitle = findViewById(R.id.warningTitle);
            warningCourseCode = findViewById(R.id.warningCourseCode);
            warningCourseName = findViewById(R.id.warningCourseName);
            warningUniversity = findViewById(R.id.warningUniversity);
            warningDescription = findViewById(R.id.warningDescription);
            warningISBN = findViewById(R.id.warningISBN);
            textview = findViewById(R.id.testView);

            txtISBN = findViewById(R.id.txtISBN);

            rgCategory = findViewById(R.id.rgCategory);
            rgBid = findViewById(R.id.rgBid);
            rbBidding = findViewById(R.id.rbBidding);
            rbSetPrice = findViewById(R.id.rbSetPrice);
            rbNotes = findViewById(R.id.rbNotes);
            rbSummary = findViewById(R.id.rbSummary);
            rbBook = findViewById(R.id.rbBook);
            parent = findViewById(R.id.parent);
            imgView = findViewById(R.id.imgView);

            setLocationButt = findViewById(R.id.addLocationListButt);

            arrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Universities)));
            Collections.sort(arrayList);
        }
}

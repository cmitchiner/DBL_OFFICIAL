package com.example.messagingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddListingActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText edtTxtTitle, edtTxtDescription, edtTxtPrice, edtTxtCourseCode, edtTxtCourseName;
    private TextView txtAddOffer, txtCategory, txtDescription, txtUploadDocument, txtUploadPicture, txtPrice, txtEuro, warningTitle, warningCourseCode, warningCourseName;
    private Spinner spinnerUniversity, spinnerCourseCode;
    private RadioGroup rgCategory, rgBid;
    private ConstraintLayout parent;
    private Button btnPublish;
    private ImageButton btnUploadPicture, btnUploadDocument;
    private ImageView imgView;
    private String Document_img1="";
    private RadioButton rbBidding, rbSetPrice;
    int SELECT_PICTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        initViews();

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPublish();
            }
        });

        btnUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnUploadDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        rbBidding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPrice.setText("Starting Price:");
            }
        });

        rbSetPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPrice.setText("Set Price: ");
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
                    }else if(options[i].equals("Take photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    }else if (options[i].equals("Choose image from gallery")){
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                }
            });
            builder.show();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode==2) {
                Uri selectedImage = data.getData();
                imgView.setImageURI(selectedImage);
            }else if (requestCode==1) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgView.setImageBitmap(imageBitmap);
            }else if (requestCode==3) {
                Uri filePath = data.getData();
                getStringPdf(filePath);
                Toast.makeText(this, "File chosen", Toast.LENGTH_SHORT).show();
            }
        }

        private void initPublish() {
            Log.d(TAG, "initPublish: started");

            if (validateData()) {
                showSnackBar();
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

        private boolean validateData() {
            if (edtTxtTitle.getText().toString().equals("")) {
//                Toast.makeText(this, "You need to add a title", Toast.LENGTH_SHORT).show();
                warningTitle.setVisibility(View.VISIBLE);
                return false;
            }
            if (edtTxtCourseCode.getText().toString().equals("")) {
                warningCourseCode.setVisibility(View.VISIBLE);
                return false;
            }
            if (edtTxtCourseName.getText().toString().equals("")) {
                warningCourseName.setVisibility(View.VISIBLE);
                return false;
            }
            return true;
        }

        private void initViews() {
            Log.d(TAG, "initViews: started");
            edtTxtTitle = findViewById(R.id.edtTxtTitle);
            edtTxtDescription = findViewById(R.id.edtTxtDescription);
            edtTxtPrice = findViewById(R.id.edtTxtPrice);
            edtTxtCourseCode = findViewById(R.id.edtTxtCourseCode);
            edtTxtCourseName = findViewById(R.id.edtTxtCourseName);

            btnPublish = findViewById(R.id.btnPublish);
            btnUploadDocument = findViewById(R.id.btnUploadDocument);
            btnUploadPicture = findViewById(R.id.btnUploadPicture);

            txtAddOffer = findViewById(R.id.txtAddOffer);
            txtCategory = findViewById(R.id.txtCategory);
            txtDescription = findViewById(R.id.txtDescription);
            txtEuro = findViewById(R.id.txtEuro);
            txtPrice = findViewById(R.id.txtPrice);
            txtUploadDocument = findViewById(R.id.txtUploadDocument);
            txtUploadPicture = findViewById(R.id.txtUploadPicture);
            warningTitle = findViewById(R.id.warningTitle);
            warningCourseCode = findViewById(R.id.warningCourseCode);
            warningCourseName = findViewById(R.id.warningCourseName);
//        searchableSpinner = findViewById(R.id.searchableSpinner);

            spinnerUniversity = findViewById(R.id.spinnerUniversity);
//        spinnerCourseCode = findViewById(R.id.spinnerCourseCode);
            rgCategory = findViewById(R.id.rgCategory);
            rgBid = findViewById(R.id.rgBid);
            rbBidding = findViewById(R.id.rbBidding);
            rbSetPrice = findViewById(R.id.rbSetPrice);
//        parent = findViewById(R.id.parent);
            imgView = findViewById(R.id.imgView);
//        list_view = findViewById(R.id.list_view);
        }
}

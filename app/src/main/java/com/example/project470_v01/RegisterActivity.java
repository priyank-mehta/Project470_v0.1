package com.example.project470_v01;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rohitarya.glide.facedetection.transformation.FaceCenterCrop;
import com.rohitarya.glide.facedetection.transformation.core.GlideFaceDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private String phoneNumber, pref_language;
    private Button btnphoto_upload;
    private ImageView imageView;
    public static final int REQUEST_IMAGE = 100;
    private static final int CAMERA_REQUEST_CODE = 1;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";
    private String imageB64 = "";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private  Button btnSubmit;
    private EditText user_name, user_dob, user_exp, addr1, addr2;
    DatePickerDialog picker;
    private String dob="";
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        findViews();
        Bundle bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("phoneNumber");
        pref_language = bundle.getString("pref_language");
        addr2.setText(bundle.getString("addr2"), TextView.BufferType.EDITABLE);
        spinner.setVisibility(View.GONE);
        Log.i("INTENTITEMS",phoneNumber);
        Log.i("INTENTITEMS",pref_language);

        mProgress = new ProgressDialog(this);

        user_dob.setInputType(InputType.TYPE_NULL);
        user_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dob = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                user_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                if ((LocalDate.now().getYear() - year)<18){
                                    Toast.makeText(RegisterActivity.this,
                                            "You are too young to work! Enjoy Life",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    REQUEST_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

//        GlideFaceDetector.initialize(getApplicationContext());
        btnphoto_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Map<String, Object> user = new HashMap<>();

                user.put("pref_language",pref_language);
                user.put("user_name", user_name.getText().toString());
                user.put("dob", dob);
                user.put("experience", user_exp.getText().toString());
                user.put("addr1", addr1.getText().toString());
                user.put("addr2", addr2.getText().toString());
//                user.put("image", imageB64);
                Log.i("FIRESTORE", user.toString());

                db.collection("userInfo").document(phoneNumber).set(user);
                startActivity(new Intent(RegisterActivity.this, SmartResume.class));
            }
        });
    }

    private void findViews() {
        imageView = findViewById(R.id.imageView);
        btnphoto_upload=findViewById(R.id.photo_upload);
        user_name = findViewById(R.id.user_name);
        user_dob = findViewById(R.id.user_dob);
        user_exp = findViewById(R.id.user_exp);
        addr1 = findViewById(R.id.addr1);
        addr2 = findViewById(R.id.addr2);
        btnSubmit=findViewById(R.id.btnSubmit);
        spinner=findViewById(R.id.progressBar);
    }

    private String readFromFile(Context context, String file_name) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file_name);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("FileNotFound", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("CannotReadFile", "Can not read file: " + e.toString());
        }

        return ret;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading Image .. ");
            mProgress.show();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            StorageReference imageRef = storageRef.child(phoneNumber);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });

            Log.i("IMAGE", extras.get("data").toString());
            imageView.setImageBitmap(imageBitmap);
//            getImageData(imageBitmap);
        }
    }

    public void getImageData(Bitmap bmp) {

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        imageB64 = Base64.encodeToString(data, Base64.DEFAULT);
        //  store & retrieve this string to firebase
        Log.i("image_str", imageB64);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
 }

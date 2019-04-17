package com.example.project470_v01;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Byte2;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.rohitarya.glide.facedetection.transformation.core.GlideFaceDetector;

import java.io.ByteArrayOutputStream;

public class CameraActivity extends AppCompatActivity {

    private Button btnphoto_upload;
    private ImageView imageView;
    public static final int REQUEST_IMAGE = 100;
    private static final int CAMERA_REQUEST_CODE = 1;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findViews();
        mProgress = new ProgressDialog(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        btnphoto_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnphoto_upload.setBackgroundColor(Color.CYAN);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading Image .. ");
            mProgress.show();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.i("IMAGE", extras.get("data").toString());
            imageView.setImageBitmap(imageBitmap);
            getImageData(imageBitmap);
            mProgress.dismiss();
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
        String imageB64 = Base64.encodeToString(data, Base64.DEFAULT);
        //  store & retrieve this string to firebase
        Log.i("image_str", imageB64);
    }

    private void findViews() {
        imageView = findViewById(R.id.imageView);
        btnphoto_upload=findViewById(R.id.photo_upload);
    }
}

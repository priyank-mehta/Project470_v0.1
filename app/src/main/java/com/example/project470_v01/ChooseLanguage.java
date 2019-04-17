package com.example.project470_v01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ChooseLanguage extends AppCompatActivity {

    private ProgressBar spinner;
    private Button btnEnglish, btnGujarati, btnHindi, btnMarathi;
    public static final int REQUEST_PERMISSION = 200;
    private JSONObject user_details = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_language);
        findViews();
        spinner.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
        }

        final Intent intent = new Intent(ChooseLanguage.this, LocationActivity.class);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        intent.putExtra("phoneNumber", bundle.getString("phoneNumber"));
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                spinner.setVisibility(View.VISIBLE);
                btnEnglish.setBackgroundColor(Color.CYAN);
                // update userInfo
                intent.putExtra("pref_language", "ENGLISH");
                startActivity(intent);
            }
        });

        btnGujarati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                spinner.setVisibility(View.VISIBLE);
                btnGujarati.setBackgroundColor(Color.CYAN);
                // update userInfo
                intent.putExtra("pref_language", "GUJARATI");
                startActivity(intent);
            }
        });

        btnHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                spinner.setVisibility(View.VISIBLE);
                btnHindi.setBackgroundColor(Color.CYAN);
                // update userInfo
                intent.putExtra("pref_language", "HINDI");
                startActivity(intent);
            }
        });

        btnMarathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                spinner.setVisibility(View.VISIBLE);
                btnMarathi.setBackgroundColor(Color.CYAN);
                // update userInfo
                intent.putExtra("pref_language", "MARATHI");
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        btnEnglish=(Button)findViewById(R.id.btnEnglish);
        btnGujarati=(Button)findViewById(R.id.btnGujarati);
        btnHindi=(Button)findViewById(R.id.btnHindi);
        btnMarathi=(Button)findViewById(R.id.btnMarathi);
        spinner=findViewById(R.id.progressBar);
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

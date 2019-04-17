package com.example.project470_v01;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 200;
    private String phoneNumber, pref_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_location);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
        }

        Bundle bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("phoneNumber");
        pref_language = bundle.getString("pref_language");

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                Log.i("got_location", location.getLatitude() + "-" + location.getLongitude());

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    JSONObject addr_obj = new JSONObject();
                    addr_obj.put("address", addresses.get(0).getAddressLine(0));
                    addr_obj.put("city",  addresses.get(0).getLocality());
                    addr_obj.put("state", addresses.get(0).getAdminArea());
                    addr_obj.put("country", addresses.get(0).getCountryName());
                    addr_obj.put("postalcode", addresses.get(0).getPostalCode());
                    Log.i("got_location", addr_obj.toString());
                    final Intent intent = new Intent(LocationActivity.this, RegisterActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("pref_language", pref_language);
                    intent.putExtra("addr1", addr_obj.getString("address"));
                    intent.putExtra("addr2", addr_obj.getString("city") + ", "+addr_obj.getString("state") + ", "+addr_obj.getString("country"));
                    startActivity(intent);
                } catch (IOException|JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
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

    private void writeToFile(String data, String file_name, Context context) {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(context.openFileOutput(file_name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}

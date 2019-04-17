package com.example.project470_v01;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class SmartResume extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_smart_resume);
//        imgview = findViewById(R.id.smart_resume);
//        imgview.setImageBitmap(getBitmapFromURL(url));
//        String url = "https://hcti.io/v1/image/ece4c95b-b735-48a4-a053-bc3726aa00a1";
//       mFunctions = FirebaseFunctions.getInstance();
//       load_resume("test string").addOnSuccessListener(new OnSuccessListener<String>() {
//           @Override
//           public void onSuccess(String responseStr) {
//               NetworkImageView profilePicture = findViewById(R.id.smart_resume);
//               profilePicture.setImageUrl(responseStr, ImageFetch.getImageLoader(getApplicationContext()));
//           }
//       }).addOnFailureListener(new OnFailureListener() {
//           @Override
//           public void onFailure(@NonNull Exception e) {
//               Toast.makeText(SmartResume.this,
//                       "Could not get response from cloud function",
//                       Toast.LENGTH_SHORT).show();
//           }
//       });

        mProgress = new ProgressDialog(this);
        Bundle bundle = getIntent().getExtras();
        showSmartResume(bundle.getString("phoneNumber"));
    }

    private void showSmartResume(String phoneNumber) {
        mProgress.setMessage("Loading your Smart Resume ..");
        mProgress.show();
        OkHttpClient httpClient = new OkHttpClient();
        HttpUrl.Builder httpBuider =
                HttpUrl.parse("https://us-central1-project470-647d6.cloudfunctions.net/helloWorld").newBuilder();
        httpBuider.addQueryParameter("phoneNumber", phoneNumber);
        Request request = new Request.Builder().
                url(httpBuider.build()).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e("HTTP", "error in getting response from firebase cloud function");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SmartResume.this,
                                "Could not get response from cloud function",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override public void onResponse(Call call, Response response){
                ResponseBody responseBody = response.body();
                String resp = "";
                if (!response.isSuccessful()) {
                    Log.e("HTTPERROR", "fail response from firebase cloud function");
                    Toast.makeText(SmartResume.this,
                            "Could not get response from cloud function",
                            Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        resp = responseBody.string();
                    } catch (IOException e) {
                        Toast.makeText(SmartResume.this,
                                "Body not readable from cloud function",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                runOnUiThread(responseRunnable(resp));
            }
        });
    }

    private Runnable responseRunnable(final String responseStr){
        Runnable resRunnable = new Runnable(){
            public void run(){
                Toast.makeText(SmartResume.this
                        ,responseStr,
                        Toast.LENGTH_SHORT).show();
                NetworkImageView profilePicture = findViewById(R.id.smart_resume);
                profilePicture.setImageUrl(responseStr, ImageFetch.getImageLoader(getApplicationContext()));
                mProgress.dismiss();
            }
        };
        return resRunnable;
    }

    private Task<String> load_resume(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("helloWorld")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

}

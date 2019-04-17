package com.example.project470_v01;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SmartResume extends AppCompatActivity {

    private ProgressDialog mProgress;
    private Button btnShare;
    private String resume_locator;
    public String imagePath;
    private FirebaseFunctions mFunctions;
    public String phoneNumber;

    public static final int REQUEST_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_smart_resume);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mFunctions = FirebaseFunctions.getInstance();

        btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                shareAsImage();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

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
        phoneNumber = bundle.getString("phoneNumber");
        Log.i("TAG", phoneNumber);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(phoneNumber);
        Log.i("TAG", imageRef.getName());

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("TAG", uri.getEncodedPath());
                Log.i("TAG", uri.getPath());
                showSmartResume(phoneNumber, "https://firebasestorage.googleapis.com" + uri.getEncodedPath() + "?alt=media");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
//        File localFile = null;
//        try {
//            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/470"); //Creates app specific folder
//            if(!path.exists()) {
//                path.mkdirs();
//            }
//            localFile = new File(path, "Resume.png"); // Imagename.png
//            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    // Local temp file has been created
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void showSmartResume(String phoneNumber, String profile_url) {
        mProgress.setMessage("Loading your Smart Resume ..");
        mProgress.show();
        OkHttpClient httpClient = new OkHttpClient();
        HttpUrl.Builder httpBuider =
                HttpUrl.parse("https://us-central1-project470-647d6.cloudfunctions.net/helloWorld").newBuilder();
        httpBuider.addQueryParameter("phoneNumber", phoneNumber);
        httpBuider.addQueryParameter("profile_url", profile_url);
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
//                Toast.makeText(SmartResume.this
//                        ,responseStr,
//                        Toast.LENGTH_SHORT).show();
                resume_locator = responseStr;
                NetworkImageView profilePicture = findViewById(R.id.smart_resume);
                profilePicture.setImageUrl(resume_locator, ImageFetch.getImageLoader(getApplicationContext()));
                new DownloadsImage().execute(resume_locator);
//                mProgress.dismiss();
            }
        };
        return resRunnable;
    }

//    private Task<String> load_resume(String text) {
//        // Create the arguments to the callable function.
//        Map<String, Object> data = new HashMap<>();
//        data.put("text", text);
//        data.put("push", true);
//
//        return mFunctions
//                .getHttpsCallable("helloWorld")
//                .call()
//                .continueWith(new Continuation<HttpsCallableResult, String>() {
//                    @Override
//                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
//                        // This continuation runs on either success or failure, but if the task
//                        // has failed then getResult() will throw an Exception which will be
//                        // propagated down.
//                        String result = (String) task.getResult().getData();
//                        return result;
//                    }
//                });
//    }

//    public static Bitmap getBitmapFromURL(String src) {
//        try {
//            Log.e("src",src);
//            URL url = new URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            Log.e("Bitmap","returned");
//            return myBitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("Exception",e.getMessage());
//            return null;
//        }
//    }

    private void shareAsLink() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Create your resume with Project 470");
        share.putExtra(Intent.EXTRA_TEXT, resume_locator);

        startActivity(Intent.createChooser(share, "Share your resume"));
    }

    private void shareAsImage() {
        String image_path;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/470"); //Creates app specific folder
        Log.e("PATH",path.getAbsolutePath());
        File file = new File(path.getAbsolutePath()+"/Resume.png");
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent .setType("image/*");
        intent .putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);

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

class DownloadsImage extends AsyncTask<String, Void,Void> {

    @Override
    protected Void doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bm = null;
        try {
            bm =    BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/470"); //Creates app specific folder
        Log.e("PATH",path.getAbsolutePath());
        if(!path.exists()) {
            path.mkdirs();
        }

        File imageFile = new File(path, "Resume.png"); // Imagename.png
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try{
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            Log.i("ExternalStorage", "-> uri=" + imageFile.getAbsolutePath());
        } catch(Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
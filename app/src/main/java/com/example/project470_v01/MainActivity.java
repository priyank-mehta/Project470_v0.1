package com.example.project470_v01;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    String phoneNumber;
    EditText etPhoneNumber;

    private static final String TAG = "PRJ_470";

    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        findViews();
        spinner.setVisibility(View.GONE);
//        Intent intent = new Intent(MainActivity.this, ChooseLanguage.class);
//        intent.putExtra("phoneNumber", phoneNumber);
//        startActivity(intent);

        StartFirebaseLogin();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if no view has focus:
                spinner.setVisibility(View.VISIBLE);
                phoneNumber="+91"+ etPhoneNumber.getText().toString();
//                phoneNumber="+919654182997";
                Log.i(TAG, phoneNumber + "is sent for verification");
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,                     // Phone number to verify
                        60,                           // Timeout duration
                        TimeUnit.SECONDS,                // Unit of timeout
                        MainActivity.this,        // Activity (for callback binding)
                        mCallback);                      // OnVerificationStateChangedCallbacks
            }
        });

        etPhoneNumber.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                if (s.length() > 10)
                {
                    hideKeyboard(MainActivity.this);
                    Toast.makeText(MainActivity.this, "Phone number cannot be more than 10 digits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
        });
    }

    private void findViews() {
        btnLogin=(Button)findViewById(R.id.btnLogin);
        etPhoneNumber=findViewById(R.id.phone_number);
        spinner=findViewById(R.id.progressBar);
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

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "signInWithCredential:success");
                            try {
                                DocumentReference docRef = db.collection("userInfo").document(phoneNumber);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Intent intent = new Intent(MainActivity.this, SmartResume.class);
                                                intent.putExtra("phoneNumber", phoneNumber);
                                                startActivity(intent);
//                                                JSONObject user_obj = new JSONObject(document.getData());
//                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d(TAG, "No such document");
                                                Intent intent = new Intent(MainActivity.this, ChooseLanguage.class);
                                                intent.putExtra("phoneNumber", phoneNumber);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
//                                if (file.exists()) {
//                                    //OPEN SMART RESUME SCREEN
//                                    startActivity(new Intent(MainActivity.this,
//                                            SmartResume.class));
//                                } else {
//                                    Intent intent = new Intent(MainActivity.this, ChooseLanguage.class);
//                                    intent.putExtra("phoneNumber", phoneNumber);
//                                    startActivity(intent);
//                                }
                                finish();
                            } catch (NullPointerException e) {
                                Toast.makeText(MainActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }
                        }
                    };
                });
    }

    private void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(MainActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
                SigninWithPhone(phoneAuthCredential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "Exception", e);
                Toast.makeText(MainActivity.this,"verification failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(MainActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

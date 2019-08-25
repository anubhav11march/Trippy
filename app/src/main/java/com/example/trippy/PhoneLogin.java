package com.example.trippy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean inProgress = false;
    private EditText phoneNumber, OTPCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        mAuth = FirebaseAuth.getInstance();
        phoneNumber = (EditText) findViewById(R.id.pnumber);
        phoneNumber.setText("+91");
        OTPCode = (EditText) findViewById(R.id.otp);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                inProgress = false;
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                inProgress = false;
            }

            @Override
            public void onCodeSent(String vid, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(vid, forceResendingToken);
                verificationId = vid;
                Log.v("AAA", "Code Sent");
            }
        };

        currentUser = mAuth.getCurrentUser();

    }
    public void verifyNumber(String phoneNumber){
        Log.v("AAA", "Phone Verification");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                10,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
        inProgress = true;
    }

    public void buttonRequestOtp(View view){
        Log.v("AAA", "requestOtp");
        String number = phoneNumber.getText().toString();
        verifyNumber(number);
    }

    public void verifyNumberWithCode(String otp, String verificationId){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithNumber(credential);
    }

    public void signInWithNumber(PhoneAuthCredential  credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.v("AAA", "Sign in Successful");
                            Toast.makeText(PhoneLogin.this, "Signed in", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Log.v("AAA", "Sign in Failed");
                    }
                });
    }

    public void phoneSignIn(View view){
        Log.v("AAA", "Sign in Clicked");
        verifyNumberWithCode(OTPCode.getText().toString(), verificationId);
    }
}

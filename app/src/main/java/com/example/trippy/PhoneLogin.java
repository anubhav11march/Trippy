package com.example.trippy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private String verificationId, number;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button button, requestOtpButton, resendOtp;
    private boolean inProgress = false;
    private EditText phoneNumber, OTPCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        mAuth = FirebaseAuth.getInstance();
        button = (Button) findViewById(R.id.buttonSignin);
        requestOtpButton = (Button) findViewById(R.id.otpbutton);
        resendOtp = (Button) findViewById(R.id.resendOtp);
        phoneNumber = (EditText) findViewById(R.id.pnumber);
        phoneNumber.setText("+91");
        OTPCode = (EditText) findViewById(R.id.otp);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                inProgress = false;
                Log.v("AAA", "Verification Complete");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.v("AAA", "Verification Failed" + e.toString());
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
        number = phoneNumber.getText().toString().trim();
        if(!TextUtils.isEmpty(number) && number.length() ==13){
            verifyNumber(number);
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }
        requestOtpButton.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resendOtp.setVisibility(View.VISIBLE);
            }
        }, 15000);
    }

    public void buttonResendOtp(View view){
        Log.v("AAA", "resendOtp");
        number = phoneNumber.getText().toString().trim();
        if(!TextUtils.isEmpty(number) && number.length() ==13){
            verifyNumber(number);
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }
        resendOtp.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resendOtp.setVisibility(View.VISIBLE);
            }
        }, 15000);
    }

    public void verifyNumberWithCode(String otp, String verificationId){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithNumber(credential);
    }

    public void signInWithNumber(final PhoneAuthCredential  credential){
//        String phoneNumber =
//        if(!TextUtils)
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.v("AAA", "Sign in Successful");
                                Toast.makeText(getApplicationContext(), number + " Signed in", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PhoneLogin.this, NameActivity.class);
                                intent.putExtra("phoneNumber", number);
                                startActivity(intent);
                            }
                            else {
                                Log.v("AAA", "Sign in Failed");
                                Toast.makeText(getApplicationContext(), "Couldn't sign in, Please check number and otp", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    public void phoneSignIn(View view){
        Log.v("AAA", "Sign in Clicked");
        button.setClickable(false);
        verifyNumberWithCode(OTPCode.getText().toString(), verificationId);
    }
}

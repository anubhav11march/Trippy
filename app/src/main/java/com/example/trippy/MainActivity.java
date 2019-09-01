package com.example.trippy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.net.CookieManager;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {



    private CallbackManager mCallbackManager;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean inProgress = false;
    private int RC_SIGNIN = 9001;
    private Button glog, fblog, pholog;
    private PhoneAuthProvider .OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationId;
//    private CallbackManager mCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        FacebookSdk.sdkInitialize(getApplicationContext());
        glog = (Button) findViewById(R.id.google);
        fblog = (Button) findViewById(R.id.fb);
        pholog = (Button) findViewById(R.id.phone);
        //google log in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("787859201122-8vc8alcb24irb9u502jrmd5dgjgbpjcc.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            Toast.makeText(this, "Signed in as: " + currentUser.getEmail(), Toast.LENGTH_LONG).show();

        //fb login
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.v("AAA", "FB Logged in " + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        Log.v("AAA", "Sign in cancelled FB");
                    }



                    @Override

                    public void onError(FacebookException error) {

                        Log.v("AAA","FB Login Error");

                    }

                }

        );





        currentUser = mAuth.getCurrentUser();
    }

    public void googleLogIn(View view){
        glog.setBackgroundDrawable(getResources().getDrawable(R.drawable.onclickbutton));
        Intent signIn = googleSignInClient.getSignInIntent();
        startActivityForResult(signIn, RC_SIGNIN);
    }

    public void phoneSignIn(View view){
        pholog.setBackgroundDrawable(getResources().getDrawable(R.drawable.onclickbutton));
        Intent intent = new Intent(MainActivity.this, PhoneLogin.class);
        startActivity(intent);
    }


    public void fblogin(View v) {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("email", "public_profile")
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(mAuth.getCurrentUser() != null)
            return;
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGNIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Signed in as: "+ account.getDisplayName(), Toast.LENGTH_LONG).show();
            }
            catch (ApiException e){
                Toast.makeText(this, "Couldn't log in", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void handleFacebookAccessToken(AccessToken token){
        Log.v("AAA", "FB Access Token");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.v("AAA", "Log in Success through FB");
                        }
                        else {
                            Log.v("AAA", "Log in through FB failed");
                        }
                    }
                });
    }





    public void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            Log.v("AAA", "Success");
                        else {
                            Log.v("AAA", "Failure");
                            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    public void checkCurrentUser(View view){
        if(mAuth.getCurrentUser()!=null)
            Toast.makeText(this, "Signed in as: " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
    }

    public void logout(View view){

        mAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
            }
        });
//        try {
//            // clearing app data
//            String packageName = getApplicationContext().getPackageName();
//            Runtime runtime = Runtime.getRuntime();
//            runtime.exec("pm clear "+packageName);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}

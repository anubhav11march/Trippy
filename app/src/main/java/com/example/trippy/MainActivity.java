package com.example.trippy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.net.CookieManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private GoogleSignInClient googleSignInClient;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
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
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Users");
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
                        Toast.makeText(getApplicationContext(), "Logged in as :", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancel() {
                        Log.v("AAA", "Sign in cancelled FB");
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    }



                    @Override

                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Log.v("AAA","FB Login Error");

                    }
                }

        );
        currentUser = mAuth.getCurrentUser();
//        hashKey();
    }

//    public void hashKey(){
//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo("com.example.trippy", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.v("AAAhash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }
//    }

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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy, hh:mm:ss a", Locale.getDefault());
                            String timestamp = sdf.format(new Date());
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Name").setValue(mAuth.getCurrentUser().getDisplayName());
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Number or Email").setValue(mAuth.getCurrentUser().getEmail());
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Last Login").setValue(timestamp);
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Login Method").setValue("Facebook");
                            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                            finish();
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
                        if(task.isSuccessful()) {
                            Log.v("AAA", "Success");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy, hh:mm:ss a", Locale.getDefault());
                            String timestamp = sdf.format(new Date());
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Name").setValue(mAuth.getCurrentUser().getDisplayName());
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Number or Email").setValue(mAuth.getCurrentUser().getEmail());
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Last Login").setValue(timestamp);
                            mRef.child(mAuth.getCurrentUser().getUid()).child("Login Method").setValue("Google");
                            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                            finish();
                        }
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

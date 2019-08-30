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

import java.net.CookieManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

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

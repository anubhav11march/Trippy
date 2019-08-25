package com.example.trippy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private int RC_SIGNIN = 9001;
    private PhoneAuthProvider .OnVerificationStateChangedCallbacks mCallbacks;
//    private CallbackManager mCallbakckManagaer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        //phone
    }

    public void googleLogIn(View view){
        Intent signIn = googleSignInClient.getSignInIntent();
        startActivityForResult(signIn, RC_SIGNIN);
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
    }

}

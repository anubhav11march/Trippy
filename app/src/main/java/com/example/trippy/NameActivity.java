package com.example.trippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NameActivity extends AppCompatActivity {
    private String number;
    private TextView user;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mRef;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        user = (TextView) findViewById(R.id.user);
        Bundle pnumber = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mRef = database.getReference().child("Users");
        name = (EditText) findViewById(R.id.name);

        if(pnumber!=null){
            number = pnumber.getString("phoneNumber");
            user.setText("Hi " + number);
        }
        else {
            number = "User";
            user.setText("Hi " + number);
        }
    }

    public void finni(View view){
        String userName = name.getText().toString().trim();
        if(!TextUtils.isEmpty(userName)){
//            String id = mRef.push().getKey();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy, hh:mm:ss a", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            mRef.child(firebaseUser.getUid()).child("Name").setValue(userName);
            mRef.child(firebaseUser.getUid()).child("Number or Email").setValue(number);
            mRef.child(firebaseUser.getUid()).child("Last Login").setValue(timestamp);
            mRef.child(firebaseUser.getUid()).child("Login Method").setValue("Phone");
            startActivity(new Intent(NameActivity.this, CalendarActivity.class));
            finish();
            Toast.makeText(this, "Added to "+ firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
        }
    }
}

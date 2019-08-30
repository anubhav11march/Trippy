package com.example.trippy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        firebaseUser = mAuth.getCurrentUser();
//        mRef = database.getReference("Users");
        name = (EditText) findViewById(R.id.name);
        database = FirebaseDatabase.getInstance();
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

        }
    }
}

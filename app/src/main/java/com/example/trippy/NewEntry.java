package com.example.trippy;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewEntry extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    FirebaseUser user;
    EditText title, note;
    String date, datePosted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        database = FirebaseDatabase.getInstance();
        Bundle bundle = getIntent().getExtras();
        date = bundle.getString("currentDate");
        datePosted = bundle.getString("datePosted");
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            finish();
        }
        user = mAuth.getCurrentUser();
        title = (EditText) findViewById(R.id.title);
        note = (EditText) findViewById(R.id.note);
        mRef = database.getReference().child("Users");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleString = title.getText().toString();
                String titleNote = note.getText().toString();
                SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                String timestamp1 = sdf1.format(new Date());
                mRef = mRef.child(user.getUid()).child("Notes").push();
                mRef.child("title").setValue(titleString);
                mRef.child("date").setValue(date);
                mRef.child("time").setValue(timestamp1);
                mRef.child("fav").setValue(0);
                mRef.child("content").setValue(titleNote);
                Toast.makeText(NewEntry.this, "Note Successfully Added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewEntry.this, CalendarActivity.class));
                finish();
            }
        });
    }

}

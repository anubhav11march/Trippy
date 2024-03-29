package com.example.trippy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateEntry extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    Context context;
    DatabaseReference mRef;
    FirebaseUser user;
    EditText title, note;
    String date, datePosted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        context = UpdateEntry.this;
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
                SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String timestamp1 = sdf1.format(new Date());
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM YYYY", Locale.getDefault());
                String timestamp2 = sdf2.format(new Date());
                mRef = mRef.child(user.getUid()).child("Notes").push();
                mRef.child("title").setValue(titleString);
                mRef.child("date").setValue(date);
                mRef.child("time").setValue(timestamp1);
                mRef.child("fav").setValue(0);
                mRef.child("content").setValue(titleNote);
                mRef.child("postdate").setValue(timestamp2);
                Toast.makeText(UpdateEntry.this, "Note Successfully Added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateEntry.this, AllNotes.class));
                Animatoo.animateFade(context);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}

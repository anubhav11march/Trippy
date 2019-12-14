package com.example.trippy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
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
    Context context;
    DatabaseReference mRef;
    FirebaseUser user;
    EditText title, note;
    String date, datePosted;
    Bundle noteU;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        noteU = getIntent().getExtras();
        final String key = noteU.getString("key");
        FloatingActionButton fab = findViewById(R.id.fab);
        if(key != null){
            context = NewEntry.this;
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            if(mAuth.getCurrentUser() == null)
                finish();
            mRef = database.getReference().child("Users");
            mRef = mRef.child(user.getUid()).child("Notes").child(key);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String ds = dataSnapshot.child("title").getValue().toString();
                    title.setText(ds);
                    note.setText(dataSnapshot.child("note").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(NewEntry.this, "An error occured", Toast.LENGTH_LONG).show();
                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRef.child("title").setValue(title.getText().toString());
                    mRef.child("note").setValue(note.getText().toString());
                }
            });
            return;
        }
        context = NewEntry.this;
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
                Toast.makeText(NewEntry.this, "Note Successfully Added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewEntry.this, AllNotes.class));
                Animatoo.animateFade(context);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(noteU != null) {
            mRef.child("title").setValue(title.getText().toString());
            mRef.child("note").setValue(note.getText().toString());
        }
        else{
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
            Toast.makeText(NewEntry.this, "Note Successfully Added", Toast.LENGTH_SHORT).show();
        }
        Animatoo.animateSlideRight(this);
    }
}

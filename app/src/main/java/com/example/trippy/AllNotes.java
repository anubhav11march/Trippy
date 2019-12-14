package com.example.trippy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.trippy.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AllNotes extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    TextView title;
    FirebaseUser currentuser;
    Context context;
    DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);
        context = AllNotes.this;
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        title = (TextView) findViewById(R.id.title);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentuser = mAuth.getCurrentUser();
        mRef = database.getReference().child("Users");
        mRef.child(currentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                title.setText("Notes for " + dataSnapshot.child("Name").getValue());
                Log.v("AAA", "data");
//                getSupportActionBar().setTitle("Welcome " + username + "!");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("AAA", "datacancel");
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("dMyyyy", Locale.getDefault());
                String timestamp = sdf.format(new Date());
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy, hh:mm:ss a", Locale.getDefault());
                String timestamp1 = sdf1.format(new Date());
                Intent intent = new Intent(AllNotes.this, NewEntry.class);
                intent.putExtra("currentDate", timestamp);
                intent.putExtra("datePosted", timestamp1);
                startActivity(intent);

                Animatoo.animateSlideUp(context);
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}
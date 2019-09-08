package com.example.trippy;

import android.content.Intent;
import android.os.Bundle;

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

import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

public class CalendarActivity extends AppCompatActivity {
//    CalendarView calendarVieww;
    MCalendarView mCalendarView;
    TextView date;
    FirebaseDatabase database;
    FirebaseUser currentuser;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    String username;
//    FloatingActionButton fab;
    long datee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Users");
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(CalendarActivity.this, MainActivity.class));
            finish();
        }
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setImageDrawable(R.drawable/fab);
        currentuser = mAuth.getCurrentUser();
//        calendarVieww = (CalendarView) findViewById(R.id.calendar);
        mCalendarView = (MCalendarView) findViewById(R.id.calendar);
        mCalendarView.markDate(2019, 9, 10);
        mCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                Toast.makeText(CalendarActivity.this, "Clicked: "+date.getMonth() + ", "+ date.getDay(), Toast.LENGTH_SHORT).show();
            }
        });
        getSupportActionBar().setTitle("Welcome");
        mRef.child(currentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("Name").getValue();
                Log.v("AAA", "data");
                getSupportActionBar().setTitle("Welcome " + username + "!");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("AAA", "datacancel");
            }
        });
        date = (TextView) findViewById(R.id.date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd - MMM - yyyy", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        date.setText("Today's Date: " + timestamp);
//        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();


//        calendarVieww.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
//                datee = calendarView.getDate();
//                Log.v("AAA", "Date change called" + datee + " " + i2 + i1 + i + " ");
//            }
//        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("dMyyyy", Locale.getDefault());
                String timestamp = sdf.format(new Date());
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy, hh:mm:ss a", Locale.getDefault());
                String timestamp1 = sdf1.format(new Date());
                Intent intent = new Intent(CalendarActivity.this, NewEntry.class);
                intent.putExtra("currentDate", timestamp);
                intent.putExtra("datePosted", timestamp1);
                startActivity(intent);
            }
        });

    }



}

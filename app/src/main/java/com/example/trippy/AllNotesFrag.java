package com.example.trippy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AllNotesFrag extends Fragment {
    FrameLayout frameLayout;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference mRef;
    FirebaseUser currentUser;

    public AllNotesFrag() {
        // Required empty public constructor
    }

    public static AllNotesFrag newInstance(String param1, String param2) {
        AllNotesFrag fragment = new AllNotesFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_notes2, container, false);
        frameLayout = (FrameLayout) view.findViewById(R.id.allNotes);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Users");
        currentUser = mAuth.getCurrentUser();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inflaterecyclerView();
        return view;
    }

    public void inflaterecyclerView(){
        FirebaseRecyclerOptions<Note > options = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(mRef.child(currentUser.getUid()).child("Notes"), Note.class)
                .build();
        FirebaseRecyclerAdapter<Note, NoteViewHolder> FBRA = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note model) {
                final String noteNo = getRef(position).getKey();
                holder.setTitle(model.getTitle());
                String date =  model.getDate() + ", " +model.getDatePosted();
                holder.setDateTime(date);
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent, false);
                return new NoteViewHolder(view);
            }
        };
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public NoteViewHolder( View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String titlee){
            TextView title = (TextView) mView.findViewById(R.id.title);
            title.setText(titlee);
        }

        public void setDateTime(String dt){
            TextView dateTime = (TextView) mView.findViewById(R.id.dates);
            dateTime.setText(dt);
        }
    }

}

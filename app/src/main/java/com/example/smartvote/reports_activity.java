package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reports_activity extends AppCompatActivity {
    private TextView summaryTitle, summaryNoVotes, summaryYesVotes, summaryNeutralVotes;
    private DatabaseReference countRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_activity);

        summaryTitle = findViewById(R.id.summaryTitleId);
        summaryTitle.setPaintFlags(summaryTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);  // code to underline a text

        summaryNoVotes = findViewById(R.id.summaryOfNoVotesId);
        summaryNoVotes.setTextColor(Color.RED);

        summaryYesVotes = findViewById(R.id.summaryOfYesVotesId);
        summaryYesVotes.setTextColor(Color.RED);

        summaryNeutralVotes = findViewById(R.id.summaryOfNeutralVotesId);
        summaryNeutralVotes.setTextColor(Color.RED);



        String pin = getIntent().getExtras().get("postKey").toString();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        countRef = FirebaseDatabase.getInstance().getReference().child("votes");


        // this code counts the number of votes and displays it .......visible to only the user
        countRef.child(pin).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String yesVotes = String.valueOf(dataSnapshot.child("Yes votes").getChildrenCount());
                summaryYesVotes.setText(yesVotes + " votes");

                String noVotes = String.valueOf(dataSnapshot.child("No votes").getChildrenCount());
                summaryNoVotes.setText(noVotes + " votes");

                String neutralVotes = String.valueOf(dataSnapshot.child("Neutral votes").getChildrenCount());
                summaryNeutralVotes.setText(neutralVotes + " votes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

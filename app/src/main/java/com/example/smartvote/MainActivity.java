package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorAdditionalInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private  DatabaseReference voteRef,postRef;
    private RecyclerView recyclerView;
    private Query query;
    private FirebaseRecyclerAdapter<queries_format,q_class> firebaseRecyclerAdapter;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        query = FirebaseDatabase.getInstance().getReference().child("posts");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");

        voteRef = FirebaseDatabase.getInstance().getReference().child("votes");
        progressDialog = new ProgressDialog(this);




        recyclerView = findViewById(R.id.recyclerViewId);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        builder = new AlertDialog.Builder(this);


        //postRef.addValueEventListener checks if any post is available..if there is no post available a message is displayed to alert user
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    progressDialog.dismiss();
                    builder.setTitle("No Info To Display");
                    builder.setMessage("Admin has not broadcasted any information");
                    //code to send a user to you tube to watch video
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onRestart();
                        }
                    });
                    builder.create().show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //method to display the queries from admin
        displayAdminQuery();



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mUser == null){
            Intent intent = new Intent(MainActivity.this,login_in_activity.class);
            startActivity(intent);
            finish();
        }

     firebaseRecyclerAdapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }

    private void displayAdminQuery() {

        progressDialog.setTitle("loading queries");
        progressDialog.setMessage("make sure you are connected to the internet");
        progressDialog.show();



        // recycler options to display admin post on the main activity
        final FirebaseRecyclerOptions<queries_format> options = new FirebaseRecyclerOptions.Builder<queries_format>()
                .setQuery(query,queries_format.class)
                .build();



            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<queries_format, q_class>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final q_class holder, final int position, @NonNull queries_format model) {




                 holder.admin.setText(model.getAdmin());
                 holder.post.setText(model.getPost());
                 holder.time.setText(model.getTime());
                 holder.date.setText(model.getDate());
                 holder.title.setText(model.getTitle());

                 if(holder.admin.getText() != null) {
                     progressDialog.dismiss();
                 }

                 //set the onBackPressed key
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);





                 final String pos = getRef(position).getKey();   // the unique of each post in a string name pos

                 // code to keep track of users who casted a neutral vote
                //TODO: write threads to handle all firebase call so as to free the main UI thread
                 voteRef.child(pos).child("Neutral votes").addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if(dataSnapshot.exists()){
                             if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                 holder.button.setEnabled(false);
                                 holder.neutralButton.setTextColor(Color.RED);
                                 holder.noButton.setVisibility(View.GONE);
                                 holder.yesButton.setVisibility(View.GONE);

                             }
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

                 // code snippets to track users who voted as yes on a post
                voteRef.child(pos).child("Yes votes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                holder.button.setEnabled(false);
                                holder.yesButton.setTextColor(Color.RED);
                                holder.neutralButton.setVisibility(View.GONE);
                                holder.noButton.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // code snippets to track users who voted no on a post
                voteRef.child(pos).child("No votes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                holder.button.setEnabled(false);
                                holder.noButton.setTextColor(Color.RED);
                                holder.yesButton.setVisibility(View.GONE);
                                holder.neutralButton.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                 // below code records vote casted by the user
                 holder.button.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         // this code is executed when the yes radio button is selected and vote is casted
                         if(holder.yesButton.isChecked()){
                             voteRef.child(pos).child("Yes votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Yes")
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful()){
                                                 /* if vote is casted successfully
                                                 * 1. the vote button will be disable
                                                 * 2. the text color of the of the radio button selected will change to red
                                                 * 3. the neutral and no radio button will be made invisible*/

                                                 holder.button.setEnabled(false);
                                                 holder.yesButton.setTextColor(Color.RED);
                                                 holder.noButton.setVisibility(View.GONE);
                                                 holder.neutralButton.setVisibility(View.GONE);
                                             }else {
                                                  // if vote is not successful an alert message is displayed to user
                                                 String message = task.getException().getMessage();
                                                 builder.setTitle("Try again");
                                                 builder.setMessage(message);
                                                 builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         onRestart();
                                                     }
                                                 });
                                                 builder.create().show();

                                             }
                                         }
                                     });
                         }else if(holder.noButton.isChecked()){
                             voteRef.child(pos).child("No votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("No")
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                /* if vote is casted successfully
                                                 * 1. the vote button will be disable
                                                 * 2. the text color of the of the radio button selected will change to red
                                                 * 3. the two other radio buttons will be rendered invisible*/
                                                holder.button.setEnabled(false);
                                                holder.noButton.setTextColor(Color.RED);
                                                holder.neutralButton.setVisibility(View.GONE);
                                                holder.yesButton.setVisibility(View.GONE);
                                            }else {
                                                String message = task.getException().getMessage();
                                                builder.setTitle("Try again");
                                                builder.setMessage(message);
                                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        onRestart();
                                                    }
                                                });
                                                builder.create().show();
                                            }
                                         }
                                     });
                         }else if(holder.neutralButton.isChecked()){
                             voteRef.child(pos).child("Neutral votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Neutral")
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful()){
                                                 /* if vote is casted successfully
                                                  * 1. the vote button will be disable
                                                  * 2. the text color of the of the radio button selected will change to red
                                                  * 3. the two other radio buttons will be rendered invisible*/
                                                 holder.button.setEnabled(false);
                                                 holder.neutralButton.setTextColor(Color.RED);
                                                 holder.yesButton.setVisibility(View.GONE);
                                                 holder.noButton.setVisibility(View.GONE);
                                             }else {
                                                 String message = task.getException().getMessage();
                                                 builder.setTitle("Try again");
                                                 builder.setMessage(message);
                                                 builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         onRestart();
                                                     }
                                                 });
                                                 builder.create().show();
                                             }
                                         }
                                     });
                         }else {
                             Toast.makeText(MainActivity.this, "please select your option", Toast.LENGTH_SHORT).show();
                         }

                     }
                 });

                 /*holder.cardview code snippet makes the card view of the post clickable
                 * 1. when clicked it navigates to the votes count activity
                 * 2. display the current post with other options*/
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Intent voteCountIntent = new Intent(MainActivity.this,votesCountActivity.class);
                         voteCountIntent.putExtra("QueryKey", pos); // intent sends the post unique key to the votes count activity
                         startActivity(voteCountIntent);
                    }
                });

            }

            @NonNull
            @Override
            public q_class onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_layout,parent,false);
                q_class holder = new q_class(view);
                return holder;
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutId:
                signOut();
                break;
            case android.R.id.home:
                onBackPressed();
                break;


            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,login_in_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    // the module class is queries_format
    //create a static class
    private static class q_class extends RecyclerView.ViewHolder{

         TextView post,date,time,admin,title;
         Button button;
         RadioGroup group;
         RadioButton yesButton,noButton,neutralButton;
         CardView cardView;
         //int selectedButtonId = -1;

        private q_class(@NonNull final View itemView) {
            super(itemView);

             button = itemView.findViewById(R.id.voteButtonId);
             cardView = itemView.findViewById(R.id.cardViewId);
             title = itemView.findViewById(R.id.theTitleId);
             title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);  // underlines the title


             post = itemView.findViewById(R.id.textId);
             date = itemView.findViewById(R.id.dateId);
             time = itemView.findViewById(R.id.timeId);
             admin = itemView.findViewById(R.id.nameOfAdminId);
             yesButton = itemView.findViewById(R.id.yesButtonId);
             noButton = itemView.findViewById(R.id.noButtonId);
             neutralButton = itemView.findViewById(R.id.neutralButtonId);
             group = itemView.findViewById(R.id.radioGroupId);


        }



    }
}



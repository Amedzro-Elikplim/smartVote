package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class votesCountActivity extends AppCompatActivity {
    private TextView titleCountText, postTextView;
    private String key, message;
    private DatabaseReference mRef, userRef, votesRefer;
    private Button editButton, deleteButton, reportButton;
    private AlertDialog.Builder builder;
    private EditText editText;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votes_count);

        editButton = findViewById(R.id.editButtonId);
        reportButton = findViewById(R.id.reportButtonId);
        deleteButton = findViewById(R.id.deleteButtonId);
        titleCountText = findViewById(R.id.queryTitleId);
        postTextView = findViewById(R.id.queryBodyId);

        titleCountText.setPaintFlags(titleCountText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        builder = new AlertDialog.Builder(this);
        dialog = new ProgressDialog(this);




       /* the code below deals with
       1. getting the post and title from firebase database to display in the votes count activity
       1. validates user status to enable or disable button ........using firebase database
       2. count votes from the firebase database and display on the report activity
        */
        key = getIntent().getExtras().get("QueryKey").toString();

        mRef = FirebaseDatabase.getInstance().getReference().child("posts");
        mRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String title = dataSnapshot.child("title").getValue().toString();
                    String body = dataSnapshot.child("post").getValue().toString();

                    titleCountText.setText(title);
                    postTextView.setText(body);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.child("status").getValue().toString().equals("Employee")){
                            editButton.setText("admin only");
                            editButton.setEnabled(false);
                            deleteButton.setEnabled(false);
                            reportButton.setEnabled(false);
                            deleteButton.setText("admin only");
                             reportButton.setText("admin only");

                    }else {
                        deleteButton.setEnabled(true);
                        editButton.setEnabled(true);
                        reportButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // code to generate report
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(votesCountActivity.this,reports_activity.class);
               intent.putExtra("postKey", key);
               startActivity(intent);
            }
        });



        // executed to edit a post
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //TODO: code for the edit button
                builder.setTitle("Edit post");
                editText = new EditText(votesCountActivity.this);
                builder.setView(editText);
                builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        message = editText.getText().toString();
                        if (TextUtils.isEmpty(message)) {
                            editText.setError("please fill this field");
                            return;
                        }
                        UpdatePost();
                    }
                });

                builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost();
            }
        });







    }

    // code to delete post
    private void deletePost() {
         builder.setTitle("Alert");
         builder.setMessage("Do you want to delete this query");
         // write code to remove the votes as well
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
               DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");
               ref.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           Toast.makeText(votesCountActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(votesCountActivity.this,admin_activity.class);
                           startActivity(intent);
                           finish();
                       }else {
                           Toast.makeText(votesCountActivity.this, "Post is not deleted successfully, try again", Toast.LENGTH_LONG).show();
                       }
                   }
               });
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 onRestart();
            }
        });
        builder.create().show();

    }

    // code is executed when the edit button is clicked and confirmed
    private void UpdatePost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");
        ref.child(key).child("post").setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(votesCountActivity.this, "post updated successfully", Toast.LENGTH_SHORT).show();
                }else {
                    String error = task.getException().getMessage();
                    builder.setTitle("Please try again");
                    builder.setMessage(error);
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

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         if(item.getItemId() == android.R.id.home){
             onBackPressed();
         }
        return super.onOptionsItemSelected(item);

    }
}

package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class publishSuggestion extends AppCompatActivity {
    private TextView text,title;
    private Button button;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,oldRef;
    private String adminName;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    private String date, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_suggestion);

        text = findViewById(R.id.publishId);
        button = findViewById(R.id.publishButtonId);
        title = findViewById(R.id.titleId);

        progressDialog = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);

        mRef = FirebaseDatabase.getInstance().getReference().child("posts");
        oldRef = FirebaseDatabase.getInstance().getReference().child("users");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mFormat = new SimpleDateFormat("dd:MM:yyyy");
        date = mFormat.format(calendar.getTime());

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        time = timeFormat.format(cal.getTime());





        oldRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    adminName = dataSnapshot.child("Full name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishAdminQuery();
            }
        });
    }

    private void publishAdminQuery() {


        String query = text.getText().toString();
        String queryTitle = title.getText().toString();

        if(TextUtils.isEmpty(query)){
            text.setError("please write the information here");
            return;
        }

        if(TextUtils.isEmpty(queryTitle)){
            title.setError("title is used in counting votes..please fill this field");
            return;
        }

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("broadcasting information");
        progressDialog.show();





        HashMap hashMap = new HashMap();
        hashMap.put("post", query);
        hashMap.put("title",queryTitle);
        hashMap.put("admin", adminName);
        hashMap.put("date",date);
        hashMap.put("time",time);

        mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + time).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(publishSuggestion.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(publishSuggestion.this, "Query uploaded successfully", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    builder.setTitle("Please try again");
                    builder.setMessage(message);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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

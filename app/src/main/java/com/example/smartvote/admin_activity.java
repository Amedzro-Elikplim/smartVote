package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.BatchUpdateException;
import java.util.Calendar;

public class admin_activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView home;
    private ImageView homeImage;
    private Button publishButton, aboutButton,logoutButton;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_activity);

        home = findViewById(R.id.homeTextId);
        homeImage = findViewById(R.id.homeImageId);
        publishButton = findViewById(R.id.publishNewInfoId);
        aboutButton = findViewById(R.id.aboutId);
        logoutButton = findViewById(R.id.logoutAdminId);


        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        builder = new AlertDialog.Builder(this);




        homeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToMainActivity();
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPublishPage();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });



        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("DEVELOPER");
                builder.setMessage("Dennis Asamoah\nAndroid app developer" );
                builder.setPositiveButton("Follow on Facebook", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNeutralButton("Follow on GitHub", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
            }
        });

    }



    private void sendUserToPublishPage() {
        Intent publishIntent = new Intent(admin_activity.this,publishSuggestion.class);
        startActivity(publishIntent);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(admin_activity.this,MainActivity.class);
        startActivity(mainIntent);


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

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(admin_activity.this,login_in_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }


}

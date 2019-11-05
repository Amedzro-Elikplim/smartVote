package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class admin_validation extends AppCompatActivity {
    private TextInputEditText adminCode;
    private Button button;
    private AlertDialog.Builder build;
    String code = "000000";  // the 6 pin digit to validate admin



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_validation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adminCode = findViewById(R.id.adminCodeId);
        button = findViewById(R.id.validateButtonId);
        build = new AlertDialog.Builder(this);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               String validCode = adminCode.getText().toString();
               //int validCodeInt = Integer.parseInt(validCode);




                if(validCode.equals(code)){
                    // if user input is equal to the 6 pin code, the user is navigated to the admin page
                    Intent intent = new Intent(admin_validation.this, admin_activity.class);
                    startActivity(intent);
                    finish();
                }else {

                    // if admin code is invalid....a dialog message pops up
                        build.setTitle("Invalid pin !!!");
                        build.setMessage("you do not have the administrative right to sign up as admin");
                        build.setPositiveButton("try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onRestart();
                                overridePendingTransition(0,0);
                            }
                        });

                        build.setNegativeButton("return", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                admin_validation.super.onBackPressed();
                            }
                        });
                        build.create().show();
                }




            }
        });

    }

}

package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class sign_up_activity extends AppCompatActivity {
    private TextInputEditText userEmail, userPassword, userConfirmPassword;

    private Button SignUpbutton, loginButton, logo;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity);

       mAuth = FirebaseAuth.getInstance();
       mUser = mAuth.getCurrentUser();


        //logo = findViewById(R.id.logoId);
        userEmail = findViewById(R.id.emailID);
        userPassword = findViewById(R.id.passwordID);
        userConfirmPassword = findViewById(R.id.confirmPasswordID);
        SignUpbutton = findViewById(R.id.buttonID);

        loginButton = findViewById(R.id.SavebuttonID);

        progressDialog = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);



         // login button on the sign up activity that sends user to the login page
         loginButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent3 = new Intent(sign_up_activity.this,login_in_activity.class);
                 intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 overridePendingTransition(0,0);
                 startActivity(intent3);
                 finish();
             }
         });



         /* the sign up button to validate and create a new user account*/
        SignUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();    // takes user email as a string
                String password = userPassword.getText().toString();  // takes user password as string
                String confirmPassword = userConfirmPassword.getText().toString(); // takes user confirm password as string


                /*if statement below does the following
                * 1. checks if the email pattern is standard
                * 2. checks for password mismatch
                * 3. checks if password length is greater than 6
                * 4. the "return" at the end, terminates the account creating process if the the conditions are not met */
                if(!isValidEmail(email) || !isValidPassword(password, confirmPassword)) return;

                     // displays a progress dialog to user
                    progressDialog.setTitle("please wait");
                    progressDialog.setMessage("creating secure account....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    /* createUserWithEmailAndPassword is a firebase method that create a new user
                    * 1. it takes email and password as parameters
                    * 2. has an onCompleteListener that triggers if the task is successful */
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();

                                // sendUserToInfoPage method is executed when the task is successful
                                SendUserToInfoPage();
                            }else{
                                // displays alert dialog message to user if the task is not successful
                                progressDialog.dismiss();
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




            }
        });




    }

    // sends user to the profile update page to input full name and other vital info.
    private void SendUserToInfoPage(){
        Intent intent = new Intent(sign_up_activity.this,user_info_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }


   // methods to validate email and password
   boolean isValidEmail(String email){
        boolean ValidEmail = !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
                if(!ValidEmail){
                    userEmail.setError("please fill this field correctly");
                    return false;
                }
                return true;
    }


    boolean isValidPassword(String password, String confirmPassword){
        if(password.isEmpty() || password.length() < 6){
            userPassword.setError("please fill this field correctly");
            return false;
        }
        if(!confirmPassword.equals(password)){
            userConfirmPassword.setError("password mismatch");
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUser != null){
            Intent intent = new Intent(sign_up_activity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_in_activity extends AppCompatActivity {
    private ImageView imageView;
    private TextInputEditText loginEmail, loginPassword;
    private Button button,signBtn;
    private TextView textView;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private AlertDialog.Builder builder;
    private RadioGroup group;
    private int checkedRadio;
    private FirebaseUser mUser;
    private DatabaseReference mRef,dataRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in_activity);

        mAuth = FirebaseAuth.getInstance();   // instance of firebase authentication
        mUser = mAuth.getCurrentUser(); // gives access to the current user using the app
        builder = new AlertDialog.Builder(this);
        dialog = new ProgressDialog(this);


        //logoBtn = findViewById(R.id.imageID);
        loginEmail = findViewById(R.id.emailId);
        loginPassword = findViewById(R.id.passwordId);
        button = findViewById(R.id.buttonId);
        textView = findViewById(R.id.text2Id);
        group = findViewById(R.id.radioGroupId);
        signBtn = findViewById(R.id.sign_up_loginId);



        /*when a user clicks sign up button on the login page, it sends the user to the sign up activity to create an account*/
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(login_in_activity.this,sign_up_activity.class);
                startActivity(intent4);
                intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(0,0);
                finish();
            }
        });


        /* 1. user inputs email address
        * 2. user enters password
        * 3. pressing the user button triggers firebase authentication*/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logUserIntoAccount method is called anytime user presses the login button
                logUserInToAccount();
            }
        });




    }

    /* onStart() method checks if user has logged out
    * if user has not logged out.... the user will be redirected to the MainActivity to read a query
    * ***this is a potential bug****....since admin must always logout and sign in before accessing the admin activity
    * will be fixed on the next version
    * and this should be run on another thread and not the main UI thread*/
    @Override
    protected void onStart() {
        super.onStart();

        if(mUser != null){
            dialog.setTitle("Please wait");
            dialog.setMessage("configuring your account");
            dialog.show();
           dataRef = FirebaseDatabase.getInstance().getReference().child("users");
           dataRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   /* this code snippet might fix the issue/bug
                   * likely to put more work on the main UI thread since it is executed on it..and not on another thread*/
                   // bug fixed
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("status").getValue().toString().equals("Administrator")){
                            dialog.dismiss();
                            Intent intent = new Intent(login_in_activity.this,admin_activity.class);
                            startActivity(intent);
                        }else {
                            dialog.dismiss();
                            Intent intent = new Intent(login_in_activity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }else {
                        dialog.dismiss();
                        Intent intent = new Intent(login_in_activity.this,user_info_activity.class);
                        startActivity(intent);
                    }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });


        }

    }

    private void logUserInToAccount() {
        String email = loginEmail.getText().toString();   // receives the user email and converts it into string
        String password = loginPassword.getText().toString();  //receives the user password and stores it as a string


        /* uses the isValidEmail and isValidPassword on line 195 and 205 respectively to validate
        * the password and email
        * "return" at the end of line 108 terminates the login process unless the email and password are valid*/
        if(!isValidEmail(email) || !isValidPassword(password)) return;



        // displays a progress dialog to the user
        dialog.setTitle("Please wait");
        dialog.setMessage("signing in");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);


        /*SignInWithEmailAndPassword is a firebase method that signs in an already authenticated user */
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    dialog.dismiss();
                    mRef = FirebaseDatabase.getInstance().getReference().child("users");
                    /* mRef.child() Event listeners checks the status of the user in the firebase database
                    * if the user is an administrator...the user is navigated to the admin activity... to publish..or generate reports of queries
                    * if the user is an Employee...the user is navigated to the main activity to read queries and vote */
                    mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.hasChild("status")){

                                    if(dataSnapshot.child("status").getValue().toString().equals("Administrator")){
                                        dialog.dismiss();
                                        Intent intent = new Intent(login_in_activity.this,admin_activity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        dialog.dismiss();
                                        Intent employeesIntent = new Intent(login_in_activity.this,MainActivity.class);
                                        startActivity(employeesIntent);
                                        finish();
                                    }

                                }


                            }else {

                                /* this code snippet navigates an authenticated user who has not updated his profile back to the user info activity
                                * to update his profile */
                                Intent profileIntent = new Intent(login_in_activity.this,user_info_activity.class);
                                startActivity(profileIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else {

                    /*code snippet displays alert dialog message to user if the login fails*/
                    dialog.dismiss();
                    String message = task.getException().getMessage();
                    builder.setTitle("Please Try again");
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

    boolean isValidEmail(String email){
        boolean ValidEmail = !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if(!ValidEmail){
            loginEmail.setError("please fill this field correctly");
            return false;
        }
        return true;
    }

    boolean isValidPassword(String password){
        if(password.isEmpty() || password.length() < 6){
            loginPassword.setError("please fill this field correctly");
            return false;
        }
        return true;
    }
}

package com.example.smartvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class user_info_activity extends AppCompatActivity {
    private Spinner spinner;
    private TextView textView;
    private TextInputEditText fullname,department,workId;
    private Button button;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userId;
    private DatabaseReference mRef;
    private ProgressDialog dialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_activity);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        builder = new AlertDialog.Builder(this);
        dialog = new ProgressDialog(this);


        spinner = findViewById(R.id.spinnerId);
        textView = findViewById(R.id.spinnerTextId);
        fullname = findViewById(R.id.fullNameId);
        department = findViewById(R.id.departmentId);
        workId = findViewById(R.id.workId);
        button = findViewById(R.id.saveButtonId);

        // drop down menu to pick work status from....
        spinner.setPrompt("select your status");
        final ArrayList<String> status = new ArrayList<>();
        status.add("click here to select status");
        status.add("Administrator");
        status.add("Employee");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,status);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // saveUserInfo() method is triggered when user presses save info button in user info activity
                saveUserInfo();
            }
        });




    }


    /* saveUserInfo checks full name, work id, department fields and save information to database ....referenced on line 53 */
    private void saveUserInfo() {
        String name = fullname.getText().toString();
        String dept = department.getText().toString();
        String id = workId.getText().toString();

        if(TextUtils.isEmpty(name)){
            fullname.setError("please provide this info");
            return;
        }

        if(TextUtils.isEmpty(dept)){
            workId.setError("please provide this info");
            return;
        }

        if(TextUtils.isEmpty(id)){
            department.setError("please provide this info");
            return;
        }

        // if status is not selected...the process terminates till the user selects his status
        if(spinner.getSelectedItem().toString().equals("click here to select status")){
            builder.setTitle("Invalid field");
            builder.setMessage("please select your status from the drop down menu");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onRestart();
                }
            });
            builder.create().show();
            return;

        }

        dialog.setTitle("Please wait");
        dialog.setMessage("saving information to database");
        dialog.show();



        //stores user info in a hash map.......before uploading to database
        HashMap hashMap = new HashMap();
        hashMap.put("status", spinner.getSelectedItem().toString());
        hashMap.put("Full name", name);
        hashMap.put("department",dept);
        hashMap.put("workId",id);

        mRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    if(spinner.getSelectedItem().toString().equals("Administrator")){
                        dialog.dismiss();

                        // if the user status is administrator, the user is navigated to validate his admin rights using a 6 pin digit
                        sendUserToValidationPage();
                    }

                    if(spinner.getSelectedItem().toString().equals("Employee")){
                        dialog.dismiss();

                        // navigates user to main page if user status is employee
                        sendUserToMainActivity();
                    }
                }else{
                    //if upload of information is not successful..error message is displayed to the user
                    dialog.dismiss();
                    String message = task.getException().getMessage();
                    builder.setTitle("Error");
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


    // intent to send employee to main page
    private void sendUserToMainActivity(){
        Intent intent = new Intent(user_info_activity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    // intent to send administrator to admin_validation page
    private void sendUserToValidationPage() {
        Intent intent = new Intent(user_info_activity.this,admin_validation.class);
        startActivity(intent);

    }

}

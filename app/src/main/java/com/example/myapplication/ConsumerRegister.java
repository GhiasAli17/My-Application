package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConsumerRegister extends AppCompatActivity {

    EditText cname;
    EditText cemail;
    EditText cPhone;
    EditText cAddress;
    EditText cPassword;
    private DatabaseReference customersDatabaseRef;
    private DatabaseReference consumerDatabasRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    private ProgressDialog loadingBar;

    private FirebaseUser currentUser;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_register);

        mAuth = FirebaseAuth.getInstance();

        cname=findViewById(R.id.ed_cname);
        cemail=findViewById(R.id.edcemail);
        cPhone=findViewById(R.id.ed_cphone);
        cAddress=findViewById(R.id.ed_caddress);
        cPassword=findViewById(R.id.ed_cPassword);
    }

    public void onclickbtnC(View view) {
        loadingBar = new ProgressDialog(this);
        Log.d("tag1","insdie onclickbtnC");

         String name=cname.getText().toString();
         String email=cemail.getText().toString();
         String phone=cPhone.getText().toString();
         String address=cAddress.getText().toString();
         String password=cPassword.getText().toString();

        loadingBar.setTitle("Please wait :");
        loadingBar.setMessage("While system is performing processing on your data...");
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    currentUserId = mAuth.getCurrentUser().getUid();
                    customersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(currentUserId);
                    customersDatabaseRef.setValue(true);
                    consumerDatabasRef=FirebaseDatabase.getInstance().getReference().child("Users")
                            .child("Consumers").child(currentUserId);
                    consumerDatabasRef.child("name").setValue(name);
                    consumerDatabasRef.child("email").setValue(email);
                    consumerDatabasRef.child("phone").setValue(phone);
                    consumerDatabasRef.child("address").setValue(address);
                    consumerDatabasRef.child("password").setValue(password);


                    Intent intent = new Intent(ConsumerRegister.this, CustomerWelcomePageActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(ConsumerRegister.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();
                }
            }
        });


    }
}
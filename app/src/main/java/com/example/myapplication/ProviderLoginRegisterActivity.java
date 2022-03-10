package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProviderLoginRegisterActivity extends AppCompatActivity {


    private TextView CreateDriverAccount;
    private Button LoginDriverButton;
    private EditText DriverEmail;
    private EditText DriverPassword;

    private DatabaseReference driversDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    private ProgressDialog loadingBar;

    private FirebaseUser currentUser;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_login_register);

        mAuth = FirebaseAuth.getInstance();


        CreateDriverAccount = (TextView) findViewById(R.id.create_driver_account);
        LoginDriverButton = (Button) findViewById(R.id.login_driver_btn);
        DriverEmail = (EditText) findViewById(R.id.driver_email);
        DriverPassword = (EditText) findViewById(R.id.driver_password);
        loadingBar = new ProgressDialog(this);



        LoginDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = DriverEmail.getText().toString();
                String password = DriverPassword.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(ProviderLoginRegisterActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(ProviderLoginRegisterActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                }

                else
                {
                  DatabaseReference  providersDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users")
                            .child("Providers");
                    loadingBar.setTitle("Please wait :");
                    loadingBar.setMessage("While system is performing processing on your data...");
                    loadingBar.show();
                    final boolean[] check = {false};


                    providersDatabaseRef.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                          ArrayList Userlist = new ArrayList<String>();
                          // Result will be holded Here
                          for (DataSnapshot dsp : snapshot.getChildren()) {

                              Log.d("tag2",dsp.child("email").getValue().toString());
                              if(email.equals(dsp.child("email").getValue().toString())){
                                  {
                                      check[0] = true;
                                      break;
                                  }
                              }
                            //add result into array list
                              Log.d("tag2","after for"+check[0]);
                              if(check[0])
                                  break;
                          }
                          if(check[0]){
                              check[0]=false;
                          mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                              @Override
                              public void onComplete(@NonNull Task<AuthResult> task)
                              {
                                  if(task.isSuccessful())
                                  {

                                      Log.d("tag2","inside success"+check[0]);

                                      Toast.makeText(ProviderLoginRegisterActivity.this, "Sign In , Successful...", Toast.LENGTH_SHORT).show();

                                      Intent intent = new Intent(ProviderLoginRegisterActivity.this, Provider1MapsActivity.class);
                                      startActivity(intent);
                                      finish();
                                      loadingBar.dismiss();
                                      return;
                                  }
                                  else{
                                      Toast.makeText(ProviderLoginRegisterActivity.this, "Error Occurred, while Signing In... ", Toast.LENGTH_SHORT).show();
                                      loadingBar.dismiss();
                                  }
                                  Log.d("tag2","after onCompplete if"+check[0]);

                              }
                          });}
                          else{
                              Toast.makeText(ProviderLoginRegisterActivity.this, "Error Occurred, while Signing In... ", Toast.LENGTH_SHORT).show();
                              loadingBar.dismiss();
                          }




                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  });


                }
            }
        });
    }

    public void onclickCreateAccount(View view) {

                Intent intent=new Intent(this,ProviderRegister.class);
                startActivity(intent);
                finish();
    }
}

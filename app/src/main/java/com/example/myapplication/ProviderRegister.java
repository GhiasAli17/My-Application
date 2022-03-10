package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

public class ProviderRegister extends AppCompatActivity {

    private DatabaseReference driversDatabaseRef;
    private DatabaseReference providersDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    EditText pname;
    EditText pemail;
    EditText pPhone;
    EditText pAddress;
    EditText pPassword;
    EditText pConfpass;
    Button btnAddLocation;
    Spinner spinner;

    private ProgressDialog loadingBar;

    private FirebaseUser currentUser;
    String currentUserId;
    String pRole="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_register);

        mAuth = FirebaseAuth.getInstance();

        spinner=findViewById(R.id.spinner);
        pname=findViewById(R.id.ed_pname);
        pemail=findViewById(R.id.edPemail);
        pPhone=findViewById(R.id.ed_phone);
        pAddress=findViewById(R.id.ed_paddress);
        pPassword=findViewById(R.id.ed_pPassword);
        btnAddLocation=findViewById(R.id.btnAddCustomLocation);

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ProviderRegister.this,ProviderCustomLocationMapsActivity.class);
                intent.putExtra("pname",pname.getText().toString());
                intent.putExtra("pemail",pemail.getText().toString());
                intent.putExtra("pPhone",pPhone.getText().toString());
                intent.putExtra("pAddress",pAddress.getText().toString());
                intent.putExtra("pPassword",pPassword.getText().toString());
                intent.putExtra("pRole",pRole);
                startActivity(intent);
            }
        });
       // pConfpass=findViewById(R.id.ed_pConfPassword);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Petrol Delivery");
        categories.add("Mechanic");
        categories.add("Denter");
        categories.add("Electrician");


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                pRole = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }


    public void onclickbtnP(View view) {
        loadingBar = new ProgressDialog(this);

        String name=pname.getText().toString();
        String email=pemail.getText().toString();
        String phone=pPhone.getText().toString();
        String address=pAddress.getText().toString();
        String password=pPassword.getText().toString();


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
                    driversDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(currentUserId);
                    driversDatabaseRef.setValue(true);
                    providersDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users")
                            .child("Providers").child(currentUserId);
                    providersDatabaseRef.child("name").setValue(name);
                    providersDatabaseRef.child("email").setValue(email);
                    providersDatabaseRef.child("phone").setValue(phone);
                    providersDatabaseRef.child("address").setValue(address);
                    providersDatabaseRef.child("password").setValue(password);
                    providersDatabaseRef.child("role").setValue(pRole);

                    Intent intent = new Intent(ProviderRegister.this, Provider1MapsActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(ProviderRegister.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();
                }
            }
        });


    }
}
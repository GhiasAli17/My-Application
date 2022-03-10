package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private Button ProviderWelcomeButton;
    private Button ConsumerWelcomeButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        ProviderWelcomeButton = (Button) findViewById(R.id.provider_welcome_btn);
        ConsumerWelcomeButton = (Button) findViewById(R.id.consumer_welcome_btn);

        ProviderWelcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent DriverIntent = new Intent(WelcomeActivity.this, ProviderLoginRegisterActivity.class);
                startActivity(DriverIntent);
            }
        });

        ConsumerWelcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent CustomerIntent = new Intent(WelcomeActivity.this, ConsumerLoginRegisterActivity.class);
                 CustomerIntent.putExtra("cus",true);
                startActivity(CustomerIntent);
            }
        });
    }



}

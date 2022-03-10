package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerWelcomePageActivity extends AppCompatActivity {

    Button btnPetrol,btnMechanic,btnDenter,btnElectrician;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_welcome_page);


        btnPetrol=findViewById(R.id.btnPetrol);
        btnElectrician=findViewById(R.id.btnElectrician);
        btnMechanic=findViewById(R.id.btnMechanic);
        btnDenter=findViewById(R.id.btnDenter);

        btnPetrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CustomerWelcomePageActivity.this, Consumer1MapsActivity.class);

                intent.putExtra("role","Petrol Delivery");
                startActivity(intent);

            }
        });

        btnElectrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerWelcomePageActivity.this, Consumer1MapsActivity.class);

                intent.putExtra("role","Electrician");
                startActivity(intent);
            }
        });

        btnMechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerWelcomePageActivity.this, Consumer1MapsActivity.class);

                intent.putExtra("role","Mechanic");
                startActivity(intent);
            }
        });

        btnDenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerWelcomePageActivity.this, Consumer1MapsActivity.class);

                intent.putExtra("role","Denter");
                startActivity(intent);
            }
        });



    }

    public void onclick(View view) {

        Intent intent = new Intent(CustomerWelcomePageActivity.this, Consumer1MapsActivity.class);

        intent.putExtra("role","Mechanic");
        startActivity(intent);


    }
}
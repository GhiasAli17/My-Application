package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        RatingBar ratingBar=findViewById(R.id.ratingBar);
        Button btnRatingSubmit=findViewById(R.id.btnRatingSubmit);
        Intent intent=getIntent();
        String customerID=intent.getStringExtra("cid");
        String providerID=intent.getStringExtra("pid");

        btnRatingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String totalStars = "Total Stars:: " + ratingBar.getNumStars();
                float rating = ratingBar.getRating();
                Log.d("rating",""+rating);
                Toast.makeText(RatingActivity.this, "Thanks for feedback", Toast.LENGTH_SHORT).show();

                DatabaseReference DriversAvailabilityRefAll = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                DriversAvailabilityRefAll.child(providerID).child("Rating").child(customerID).setValue(rating);

            }
        });

    }
}
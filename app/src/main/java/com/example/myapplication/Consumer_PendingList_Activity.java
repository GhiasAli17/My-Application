package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Consumer_PendingList_Activity extends AppCompatActivity {

    ArrayList<MyOwnModel> val;
    RecyclerView r1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_pending_list);

        r1=findViewById(R.id.recyclerview);

        Log.d("gh","called");

        Intent intent=getIntent();
        String name=intent.getStringExtra("providerID");

        MyOwnModel obj=new MyOwnModel("Mechanic Ustad","Mechanic");
        val=obj.createList(name);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                Log.d("gh",val+"");
                MyOwnAdapter adp=new MyOwnAdapter(getApplicationContext(),val);
                r1.setAdapter(adp);
                r1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }
        }, 2000);





    }
}
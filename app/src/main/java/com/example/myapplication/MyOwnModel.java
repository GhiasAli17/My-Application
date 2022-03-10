package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyOwnModel {
    String name;
    String service;


    public MyOwnModel(String name,String service){
        this.name=name;
        this.service=service;


    }

    public ArrayList<MyOwnModel> createList(String name){
        ArrayList<MyOwnModel> lists=new ArrayList<MyOwnModel>();
        Log.d("cat1","cat1 called inside arraylist creation"+name);

        DatabaseReference ProviderLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working").child(name);
        ProviderLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp: snapshot.getChildren()){
                    Log.d("cat","key......"+dsp.getKey().toString());
                    DatabaseReference cRef=FirebaseDatabase.getInstance().getReference().
                            child("Users").child("Consumers").child(dsp.getKey());
                    cRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dsp1: dataSnapshot.getChildren()){
                                String name=dsp1.getKey().toString();
                                if(name.equals("name")){
                                    lists.add(new MyOwnModel((dsp1.getValue(String.class)+"_"+dsp.getKey().toString()),"customer"));
                                }
                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    //lists.add(new MyOwnModel("name "+dsp.getKey().toString(),"mechanic"));
                    Log.d("cat","Lists"+lists);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        ArrayList<MyOwnModel> lists=new ArrayList<MyOwnModel>();
//        for(int i=0;i<5;i++){
//            lists.add(new MyOwnModel("name "+i,"service "+i));
//        }
        return  lists;
    }

}

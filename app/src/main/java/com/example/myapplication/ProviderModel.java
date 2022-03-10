package com.example.myapplication;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProviderModel {
    String rating;
    String phone;
    int distance;
    String providerID;

    public ProviderModel(String rating, int distance,String providerID,String phone) {
        this.rating = rating;
        this.providerID=providerID;
        this.distance = distance;
        this.phone=phone;
    }

    public ArrayList<ProviderModel> createList(String customerID){
        final int[] clat1 = {0};
        final int[] clon1 = {0};
        final Location[] location1 = new Location[1];

        ArrayList<ProviderModel> lists=new ArrayList<ProviderModel>();
        final boolean[] retCheck = {false};

        DatabaseReference customerDb=FirebaseDatabase.getInstance().getReference().child("Customer Requests")
                .child(customerID).child("l");
        customerDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 int count=0;
                 Log.d("dist","in");
                for(DataSnapshot dsp: snapshot.getChildren()){
                         if(count==0){
                             int clat=dsp.getValue(int.class);
                             clat1[0] =clat;
                         }
                         else{
                             int clon=dsp.getValue(int.class);
                             clon1[0] =clon;
                         }
                         count++;

                }

                location1[0] =new Location("");
                location1[0].setLatitude(clat1[0]);
                location1[0].setLongitude(clon1[0]);


                DatabaseReference ProviderAllDb=FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                ProviderAllDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        for(DataSnapshot dsp1:snapshot1.getChildren()){
                           int plat= dsp1.child("l").child("0").getValue(int.class);
                           int plon=dsp1.child("l").child("1").getValue(int.class);
                           Location location2=new Location("");
                           location2.setLatitude(plat);
                           location2.setLongitude(plon);

                           float dist=location1[0].distanceTo(location2);
                           Log.d("dis",""+dist);

                            lists.add(new ProviderModel("3.0 ",distance,dsp1.getKey().toString(),""));


                        }

                        retCheck[0] =true;



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });



        if(retCheck[0]){
            return  lists;
        }
        else{
            return  lists;
        }




    }
}

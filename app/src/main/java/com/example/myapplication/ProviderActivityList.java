package com.example.myapplication;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProviderActivityList extends AppCompatActivity {

    ArrayList<ProviderModel> val;
    RecyclerView r1;
    String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);

        Log.d("new one"," ghias"+ FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

        String currentCustomerID=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        r1=findViewById(R.id.recyclerview1);
        Intent intent=getIntent();
        role=intent.getStringExtra("role");

        //ProviderModel obj=new ProviderModel("Cat",2,);
        ///////////////////////////////////////////


        final int[] clat1 = {0};
        final int[] clon1 = {0};
        final Location[] location1 = new Location[1];

        ArrayList<ProviderModel> lists=new ArrayList<ProviderModel>();
        final boolean[] retCheck = {false};

        DatabaseReference customerDb= FirebaseDatabase.getInstance().getReference().child("Customer Requests")
                .child(currentCustomerID).child("l");
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

                final boolean[] checkRating = {false};

                DatabaseReference ProviderAllDb=FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                ProviderAllDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        for(DataSnapshot dsp1:snapshot1.getChildren()){

                            //role based list
                            DatabaseReference roleReference=FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child("Providers").child(dsp1.getKey().toString());

                            roleReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot7) {

                                    for(DataSnapshot dsp7:snapshot7.getChildren()){
                                        if(dsp7.getKey().toString().equals("role")){
                                            String cRole=dsp7.getValue(String.class);
                                            if(cRole.equals(role)){

                                                int plat= dsp1.child("l").child("0").getValue(int.class);
                                                int plon=dsp1.child("l").child("1").getValue(int.class);
                                                Location location2=new Location("");
                                                location2.setLatitude(plat);
                                                location2.setLongitude(plon);

                                                int dist= (int) location1[0].distanceTo(location2);
                                                Log.d("dis",""+dist);
                                                int rating=0;



                                                if(dsp1.hasChild("Rating")){
                                                    Log.d("ratingIf",""+dist);
                                                    DatabaseReference providerCurrent=FirebaseDatabase.getInstance().getReference()
                                                            .child("Drivers Available").child(dsp1.getKey().toString()).child("Rating");

                                                    providerCurrent.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                            int sum=0;
                                                            int count1=0;
                                                            for(DataSnapshot dsp2:snapshot2.getChildren()){
                                                                sum+=dsp2.getValue(int.class);
                                                                Log.d("sum","key"+dsp2.getKey()+" "+dsp2.getValue(int.class));
                                                                count1++;
                                                            }
                                                            if(count1==0){
                                                                count1=1;
                                                            }
//

                                                            DatabaseReference providerRefPhone=FirebaseDatabase.getInstance().getReference()
                                                                    .child("Users").child("Providers").child(dsp1.getKey().toString());

                                                            int finalSum = sum;
                                                            int finalCount = count1;
                                                            providerRefPhone.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                                                    boolean cPhoneCheck=false;
                                                                    boolean cNameCheck=false;
                                                                    String cPhone="";
                                                                    String cName="";

                                                                    for(DataSnapshot dsp3:snapshot3.getChildren()){


                                                                        if(dsp3.getKey().toString().equals("phone")){
                                                                            cPhoneCheck=true;
                                                                            cPhone=dsp3.getValue(String.class);
//                                                                            lists.add(new ProviderModel(""+ finalSum / finalCount,dist,
//                                                                                    dsp1.getKey().toString(),dsp3.getValue(String.class)));
//
//                                                                            Log.d("phone",dsp3.getValue(String.class));
//                                                                            break;
                                                                        }
                                                                        if(dsp3.getKey().toString().equals("name")){
                                                                            cNameCheck=true;
                                                                            cName=dsp3.getValue(String.class);
//                                                                            lists.add(new ProviderModel(""+ finalSum / finalCount,dist,
//                                                                                    dsp1.getKey().toString(),dsp3.getValue(String.class)));
//
//                                                                            Log.d("phone",dsp3.getValue(String.class));
//                                                                            break;
                                                                        }
                                                                        if(cPhoneCheck && cNameCheck){
                                                                            lists.add(new ProviderModel(""+ finalSum / finalCount,dist,
                                                                                    cName,cPhone));
                                                                            break;

                                                                        }

                                                                    }

                                                                    checkRating[0] =true;
                                                                    ProviderAdapter adp=new ProviderAdapter(ProviderActivityList.this,lists);
                                                                    r1.setAdapter(adp);
                                                                    r1.setLayoutManager(new LinearLayoutManager(ProviderActivityList.this));

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
                                                }
                                                else{
                                                    DatabaseReference providerRefPhone=FirebaseDatabase.getInstance().getReference()
                                                            .child("Users").child("Providers").child(dsp1.getKey().toString());


                                                    providerRefPhone.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot3) {

                                                            for(DataSnapshot dsp3:snapshot3.getChildren()){
                                                                if(dsp3.getKey().toString().equals("phone")){
                                                                    lists.add(new ProviderModel("0",dist,
                                                                            dsp1.getKey().toString(),dsp3.getValue(String.class)));

                                                                    Log.d("phone",dsp3.getValue(String.class));
                                                                    break;
                                                                }
                                                            }

                                                            if(checkRating[0]==false){

                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }

                                                break;

                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        // put here condition if no one has rating

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

    }
}
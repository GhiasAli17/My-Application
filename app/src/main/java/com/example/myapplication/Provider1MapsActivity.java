package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.databinding.ActivityProvider1MapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

///////////
//////////////

public class Provider1MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location LastLocation;
    LocationRequest locationRequest;
    public boolean check=false;
    Polyline polyline=null;
    List<LatLng> latLngList=new ArrayList<>();
    List<Marker> markerList=new ArrayList<>();


    private Button LogoutProviderBtn;
    private Button SettingsDriverButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentLogOutUserStatus = false;
    SearchView searchView;
    Button pending,btnOrderComplete;
    Button btnDraw;

    //getting request customer's id
    private String customerID = "";
    private String providerID;
    private DatabaseReference AssignedCustomerRef;
    private DatabaseReference AssignedCustomerPickUpRef;
    Marker PickUpMarker;

    private ValueEventListener AssignedCustomerPickUpRefListner;

    private TextView txtName, txtPhone;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;

    private ActivityProvider1MapsBinding binding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       String title= (String) item.getTitle();
        switch (title){
            case "Pending Order":
                Toast.makeText(Provider1MapsActivity.this, title, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Provider1MapsActivity.this,Consumer_PendingList_Activity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(Provider1MapsActivity.this, title, Toast.LENGTH_SHORT).show();
                break;
        }
             return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProvider1MapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        providerID = mAuth.getCurrentUser().getUid();
        pending=findViewById(R.id.pending);
        btnOrderComplete=findViewById(R.id.btnOrderComplete);


        btnOrderComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference DriversWorkingRefSetCheck = FirebaseDatabase.getInstance().getReference().
                        child("Drivers Working").child(providerID).child(customerID);
                DriversWorkingRefSetCheck.setValue(false);
                SharedPreferences ProviderCheckCompleted=getSharedPreferences("checkCompletion",MODE_PRIVATE);
                SharedPreferences.Editor editor2=ProviderCheckCompleted.edit();
                editor2.putString("providerID",providerID);
                editor2.apply();
              //  DriversWorkingRef.removeValue();

                Toast.makeText(Provider1MapsActivity.this, "Completed", Toast.LENGTH_SHORT).show();



            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Provider1MapsActivity.this,Consumer_PendingList_Activity.class);
                intent.putExtra("providerID",providerID);
                startActivity(intent);
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(51.5, -0.1), new LatLng(40.7, -74.0))
                .width(5)
                .color(Color.RED));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("tag"," if onMap Ready Provider map");

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
            },1234);

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            Log.d("tag"," if onMap Ready Provider map");

            requestPermissions(new String[]{Manifest.permission.CALL_PHONE
            },123);

        }
        Log.d("tag","after if onMap Ready Provider map");

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //

            return;
        }
        //it will handle the refreshment of the location
        //if we dont call it we will get location only once

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {

            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LastLocation = location;

        if(!check) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Provider"));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            check=true;
        }


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);


//       DriversAvailabilityRef.child(userID).child("Rating").child("0").setValue(0);



        // come from list request
        SharedPreferences pref=getSharedPreferences("pref",MODE_PRIVATE);
        String currentCustomerID=pref.getString("CurrentCustomerID1","default");

        Log.d("now","here: "+currentCustomerID);
        customerID=currentCustomerID;

//        Intent intent1=getIntent();
//        customerID=intent1.getStringExtra("CurrentCustomerID");
//        //now done
       // customerID="";



        //////////////////////////////////////
        DatabaseReference cdatabaseReference=FirebaseDatabase.getInstance().getReference().child("Customer Requests");
        DatabaseReference DriversWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working").child(userID);

        GeoFire geoFireWorking = new GeoFire(DriversWorkingRef);
        final Polyline[] line = {null};

        Log.d("now",customerID);

        switch (customerID)
        {
            case "default":
                Log.d("tag3","in switch case null");

                geoFireWorking.removeLocation(userID);
                geoFireAvailability.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dsp:snapshot.getChildren()){
                            if(dsp.hasChild("Rating")){
                                // do nothing
                            }
                            else{
                                dbRef.child(userID).child("Rating").child("0").setValue(0);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;

            default:
                //geoFireAvailability.removeLocation(userID);
               // geoFireWorking.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                Log.d("tag6","default called");

                cdatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dsp: snapshot.getChildren()){
                            String cid1=dsp.getKey().toString();
                            Log.d("customerID in customer",cid1);
                            Log.d("passid in customer",customerID);
                            if(customerID.equals(cid1)){

                                Double cLat = Double.parseDouble(dsp.child("l").child("0").getValue().toString());
                               Double cLong=Double.parseDouble(dsp.child("l").child("1").getValue().toString());
                                LatLng latLngC = new LatLng(cLat, cLong);

                                Location location1 = new Location("");
                                location1.setLatitude(cLat);
                                location1.setLongitude(cLong);

                                Location location2 = new Location("");
                                location2.setLatitude(location.getLatitude());
                                location2.setLongitude(location.getLongitude());

                                float Distance = location1.distanceTo(location2);
                                Log.d("rg",""+Distance);
                                mMap.addMarker(new MarkerOptions().position(latLngC).title(""+cid1).icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
                                if(line[0]==null)
                                line[0] = mMap.addPolyline(new PolylineOptions()
                                        .add(new LatLng(location.getLatitude(),location.getLongitude()),
                                                latLngC)
                                        .width(5)
                                        .color(Color.RED).geodesic(true)
                                );
                                break;
                            }
                            else{
                                Log.d("rg1","in else case of custermer");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }


    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }
}
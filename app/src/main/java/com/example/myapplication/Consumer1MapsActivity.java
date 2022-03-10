package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.databinding.ActivityConsumer1MapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Consumer1MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location LastLocation;
    LocationRequest locationRequest;

    Double abc = 1.0;
    int c = 0;
    private Button Logout;
    private Button SettingsButton;
    private Button CallServiceButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference CustomerDatabaseRef;
    private LatLng CustomerPickUpLocation;

    private DatabaseReference DriverAvailableRef, ProviderLocationRef;
    private DatabaseReference ProviderRef;
    private double radius = 1;

    private Boolean providerFound = false, requestType = false;
    private String ProviderFoundID;
    private String customerID;
    Marker ProviderMarker, PickUpMarker;
    GeoQuery geoQuery;
    Button btnProviderList;

    private ValueEventListener ProviderLocationRefListner;


    private TextView txtName, txtPhone, txtCarName;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;
    final double[] pLat = new double[1];
    final double[] pLong = new double[1];
    final int[] count = new int[1];
    String role="";


    private ActivityConsumer1MapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConsumer1MapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customer Requests");
        DriverAvailableRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        ProviderLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
        btnProviderList=findViewById(R.id.btnProviderList);
        Intent intent=getIntent();
        role=intent.getStringExtra("role");

        SharedPreferences pref = this.getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CurrentCustomerID", customerID);
        editor.apply();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


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

        btnProviderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Intent intent4=new Intent(Consumer1MapsActivity.this,ProviderActivityList.class);
                  intent4.putExtra("role",role);
                  startActivity(intent4);
                  finish();
            }
        });


        Log.d("tag", "after if onMap Ready Provider map");

        buildGoogleApiClient();

        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //

            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            //

            return;
        }
        //it will handle the refreshment of the location
        //if we dont call it we will get location only once
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


        GeoFire consumerLocactionGeoFire=new GeoFire(CustomerDatabaseRef);
        consumerLocactionGeoFire.setLocation(customerID,new GeoLocation(location.getLatitude(),location.getLongitude()));


        final boolean[] check = {false};
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(),marker.getTitle(),Toast.LENGTH_LONG).show();

                 final AlertDialog.Builder alert=new AlertDialog.Builder(Consumer1MapsActivity.this);
                 alert.setMessage("Do you want to send the request");
                 alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         ProviderLocationRef.child(marker.getTitle()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                         check[0] =true;
                         Intent intent=new Intent(Consumer1MapsActivity.this,Consumer_PendingList_Activity.class);
                         intent.putExtra("pid",marker.getTitle());
                       //  startActivity(intent);

                         Toast.makeText(Consumer1MapsActivity.this,"Your request has been sent",Toast.LENGTH_LONG).show();


                     }
                 });
                 alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                 });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                   return false;
            }
        });

        LastLocation = location;


        DatabaseReference DriversAvailabilityRefAll = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        DriversAvailabilityRefAll.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    String pid1=dsp.getKey().toString();

                    DatabaseReference roleReference=FirebaseDatabase.getInstance().getReference().child("Users")
                            .child("Providers").child(pid1);
                    roleReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot6) {
                            for(DataSnapshot dsp6:snapshot6.getChildren()){
                                if(dsp6.getKey().toString().equals("role")){
                                    String cRole=dsp6.getValue(String.class);
                                    if(cRole.equals(role)){
                                        Log.d("rg",pid1);
                                        pLat[0] = Double.parseDouble(dsp.child("l").child("0").getValue().toString());
                                        pLong[0]=Double.parseDouble(dsp.child("l").child("1").getValue().toString());
                                        LatLng latLngP = new LatLng(pLat[0], pLong[0]);


                                        Location location1 = new Location("");
                                        location1.setLatitude(pLat[0]);
                                        location1.setLongitude(pLong[0]);

                                        Location location2 = new Location("");
                                        location2.setLatitude(location.getLatitude());
                                        location2.setLongitude(location.getLongitude());

                                        float Distance = location1.distanceTo(location2);
                                        Log.d("rg",""+Distance);
                                        switch (role){
                                            case "Mechanic":
                                                mMap.addMarker(new MarkerOptions().position(latLngP).
                                                        title(""+pid1).icon(BitmapDescriptorFactory.
                                                        fromResource(R.drawable.mechanic)));
                                                break;
                                            case "Electrician":
                                                mMap.addMarker(new MarkerOptions().position(latLngP).
                                                        title(""+pid1).icon(BitmapDescriptorFactory.
                                                        fromResource(R.drawable.electrician)));
                                                break;
                                            case "Denter":
                                                mMap.addMarker(new MarkerOptions().position(latLngP).
                                                        title(""+pid1).icon(BitmapDescriptorFactory.
                                                        fromResource(R.drawable.denter)));
                                                break;
                                            default:
                                                mMap.addMarker(new MarkerOptions().position(latLngP).
                                                        title(""+pid1).icon(BitmapDescriptorFactory.
                                                        fromResource(R.drawable.car)));
                                                break;

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
                    // break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Consumer").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        Log.d("tag9","laaaaat"+pLat[0]);
        Log.d("tag9","Long"+pLong[0]);


        SharedPreferences DriverWorkingCheckRef=getSharedPreferences("checkCompletion",MODE_PRIVATE);
        final String[] providerAcceptedID = {DriverWorkingCheckRef.getString("providerID", "")};

        Log.d("now pid: ", providerAcceptedID[0]);


        DatabaseReference DriverWorkingCheckCompletion=FirebaseDatabase.getInstance().getReference().
                child("Drivers Working");

        DriverWorkingCheckCompletion.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dsp2:snapshot.getChildren()){
                    if(dsp2.hasChild(customerID)){
                        boolean pCheck =dsp2.child(customerID).getValue(boolean.class);
                        if(!pCheck){
                            Log.d("now","here");
                            Intent intent3=new Intent(Consumer1MapsActivity.this,RatingActivity.class);
                            intent3.putExtra("cid",customerID);
                            intent3.putExtra("pid",dsp2.getKey().toString());
                            startActivity(intent3);

                            // just for the sake to not call the Ratingactivity again and again

                            DriverWorkingCheckCompletion.child(dsp2.getKey()).child(customerID).setValue(true);
                            finish();
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
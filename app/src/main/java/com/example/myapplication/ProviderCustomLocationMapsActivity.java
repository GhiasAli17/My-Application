package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityProviderCustomLocationMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProviderCustomLocationMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityProviderCustomLocationMapsBinding binding;
    SearchView searchView;


    private DatabaseReference driversDatabaseRef;
    private DatabaseReference providersDatabaseRef;
    private FirebaseAuth mAuth;
    Button btnCreateCustomLocation;
    String currentUserId="default";
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProviderCustomLocationMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchView=findViewById(R.id.searchview);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mAuth = FirebaseAuth.getInstance();
        Intent intent=getIntent();
        loadingBar = new ProgressDialog(this);

//        loadingBar.setTitle("Please wait :");
//        loadingBar.setMessage("While system is performing processing on your data...");
//        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(intent.getStringExtra("pemail"), intent.getStringExtra("pPassword")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                    providersDatabaseRef.child("name").setValue(intent.getStringExtra("pname"));
                    providersDatabaseRef.child("email").setValue(intent.getStringExtra("pemail"));
                    providersDatabaseRef.child("phone").setValue(intent.getStringExtra("pPhone"));
                    providersDatabaseRef.child("address").setValue(intent.getStringExtra("pAddress"));
                    providersDatabaseRef.child("password").setValue(intent.getStringExtra("pPassword"));
                    providersDatabaseRef.child("role").setValue(intent.getStringExtra("pRole"));


                }
                else
                {
                    Toast.makeText(ProviderCustomLocationMapsActivity.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                   // loadingBar.dismiss();
                }
            }
        });


        //////////////////////////

        /////////////////////

        btnCreateCustomLocation=findViewById(R.id.btnCreateCustomLocation);
        btnCreateCustomLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location1=searchView.getQuery().toString();
                List<Address> addressList=null;
                if(location1!=null || !location1.equals("")){
                    Geocoder geocoder=new Geocoder(ProviderCustomLocationMapsActivity.this);
                    try{
                        addressList=geocoder.getFromLocationName(location1,1);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    Address address=addressList.get(0);
                    LatLng latLng1=new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng1).title(location1));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,12));

                    DatabaseReference DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                    GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);
                    geoFireAvailability.setLocation(currentUserId, new GeoLocation(address.getLatitude(), address.getLongitude()));

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
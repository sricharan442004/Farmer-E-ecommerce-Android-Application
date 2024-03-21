package com.example.farmezy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FinalPlaceOrderActivity extends AppCompatActivity {

    private EditText finalmn, finalname;
    private Button finalPlaceOrderButton;
    private FusedLocationProviderClient fusedLocationClient;
    private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_place_order);

        finalmn = findViewById(R.id.finalmn);
        finalname = findViewById(R.id.finalname);
        finalPlaceOrderButton = findViewById(R.id.finalPlaceOrderButton);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrieve the previously entered mobile number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString("MobileNumber", "");
        if (!mobileNumber.isEmpty()) {
            finalmn.setText(mobileNumber);
            finalmn.setEnabled(false); // Set the EditText as non-editable
        }

        finalPlaceOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }
    private void placeOrder() {
        getCurrentLocation();
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Reverse geocoding to get the address
                        Geocoder geocoder = new Geocoder(FinalPlaceOrderActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (!addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String userLocation = address.getAddressLine(0); // Complete address
                                String userDistrict = address.getSubAdminArea(); // District

                                String fullAddress = userLocation + ", " + userDistrict;

                                // Use the address for further processing
                                processOrder(fullAddress);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(FinalPlaceOrderActivity.this, "Unable to retrieve current location.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void processOrder(String address) {
        String name = finalname.getText().toString();
        String consumermn = finalmn.getText().toString();
        String tot = getIntent().getStringExtra("totalprice");
        ArrayList<String> cartItems = getIntent().getStringArrayListExtra("cartItems");

        if (!consumermn.matches("[0-9]{10}")) {
            finalmn.setError("Enter a valid number");
        } else if (name.isEmpty()) {
            finalname.setError("Enter a valid name");
        } else {
            // Save the mobile number to SharedPreferences if it is not already saved
            if (mobileNumber.isEmpty()) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("MobileNumber", consumermn);
                editor.apply();
            }

            MyUtility utility = new MyUtility();
            utility.placeorder(FinalPlaceOrderActivity.this, name, consumermn, address, tot, cartItems);
        }
    }
}
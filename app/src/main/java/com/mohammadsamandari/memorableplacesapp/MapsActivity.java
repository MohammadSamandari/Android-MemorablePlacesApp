package com.mohammadsamandari.memorableplacesapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    String[] locationFromIntent;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //  Getting the Information from Main Activity.
        Intent intent = getIntent();
        locationFromIntent = intent.getStringArrayExtra("location");

        //  Defining Location Manage:
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //  Defining Location Listener:
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                goToThisLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String latFromIntent = locationFromIntent[0];
        String lngFromIntent = locationFromIntent[1];

        if (locationFromIntent[3].equals("0")) {
            //  Moving to myLocation Because the first item on listview has been clicked.
            requestLocation();
        } else {
            //  Creating Location Variable to move to.
            Location location = new Location("locationFromIntent");
            location.setLatitude(Double.parseDouble(latFromIntent));
            location.setLongitude(Double.parseDouble(lngFromIntent));
            goToThisLocation(location);
        }

        //  Confiquring on map long click listener.
        //  when user long clicks on a location on the map, the location is added to the list view.
        mMap.setOnMapLongClickListener(this);

        //  Configuring on map click listener
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            Toast.makeText(this, "We Down't Have Permission To Get Your Location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //  Moving to the selection place on the map
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        getInfoFromThisLocationAndSaveIt(latLng);
    }

    private void getInfoFromThisLocationAndSaveIt(LatLng latLng) {
        try {
            //  Extracting Address from location.
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String countryName = addresses.get(0).getCountryName();
            String[] addressLine = addresses.get(0).getAddressLine(0).split(",");
            String address = addressLine[0];
            Log.i("Lord_Address", addressLine[0]);

            //  Adding information about this location to the arraylists.
            Log.i("Lord-AddingPlace", countryName + address + "/" + latLng.latitude + "/" + latLng.longitude);
            MainActivity.memorablePlacesNames.add(countryName + " - " + address);
            MainActivity.memorablePlacesLat.add(String.valueOf(latLng.latitude));
            MainActivity.memorablePlacesLng.add(String.valueOf(latLng.longitude));

            //  updating the list view of that activity.
            MainActivity.memorablePlacesArrayAdapter.notifyDataSetChanged();

            //  Notifing the user that the place has been added.
            Toast.makeText(this, "New Place Has Been Added", Toast.LENGTH_SHORT).show();

            //  Updating Shared Preferences. Saving new Information into shared preferences.
            MainActivity.sharedPreferences.edit().putString("memorablePlacesNames",ObjectSerializer.serialize(MainActivity.memorablePlacesNames)).apply();
            MainActivity.sharedPreferences.edit().putString("memorablePlacesLat",ObjectSerializer.serialize(MainActivity.memorablePlacesLat)).apply();
            MainActivity.sharedPreferences.edit().putString("memorablePlacesLng",ObjectSerializer.serialize(MainActivity.memorablePlacesLng)).apply();

            //  Closing the Map Activity
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestLocation() {
        //  Getting MyLocation.
        //  Checking to have Permission for location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //  Requesting for location only once.
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,null);
            // Moving Camera to My Location in the location listener result.
        } else {
            //  Asking for location Permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void goToThisLocation(Location location) {
        //  Moving the Camera to this Location
        String nameFromIntent = locationFromIntent[2];

        //  This Condition is true when the first item on listview is clicked and we
        //  are moving to users current location.
        if (nameFromIntent.equals("Add a new place . . .")) {
            nameFromIntent = "You Are Here";
        }

        LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(locationLatLng).title(nameFromIntent));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 13));
    }
}

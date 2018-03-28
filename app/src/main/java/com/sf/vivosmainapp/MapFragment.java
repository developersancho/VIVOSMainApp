package com.sf.vivosmainapp;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private List<Metro> metroList = new ArrayList<>();

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    private Location mLastLocation;
    final static int REQUEST_LOCATION = 199;
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RES_REQUEST = 300193;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        getMetroFromJson();
        return rootView;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setZoomControlsEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                //buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                //Request Location Permission
                checkPermissions();
            }
        } else {
            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocation();
                }
        }
    }


    private void getMetroFromJson() {
        try {
            metroList.clear();
            String jsonDataString = readJsonDataFromFile();
            JSONArray parkItemsJsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < parkItemsJsonArray.length(); ++i) {
                JSONObject parkItemObject = parkItemsJsonArray.getJSONObject(i);

                String code = parkItemObject.getString("code");
                String name = parkItemObject.getString("name");
                String type = parkItemObject.getString("type");
                String postcode = parkItemObject.getString("postcode");
                String city = parkItemObject.getString("city");
                String town = parkItemObject.getString("town");
                String neighborhood = parkItemObject.getString("neighborhood");
                String xCoor = parkItemObject.getString("xCoor");
                String yCoor = parkItemObject.getString("yCoor");
                String address = parkItemObject.getString("address");

                Metro metro = new Metro(code, name, type, postcode, city, town, neighborhood, yCoor, xCoor, address);
                metroList.add(metro);
            }

        } catch (IOException | JSONException t) {
            Toast.makeText(getContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MainActivity.class.getName(), "Unable to parse JSON file.", t);
        }

    }

    private String readJsonDataFromFile() throws IOException {
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = getResources().openRawResource(R.raw.metro);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }


    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    private void getMyLocation() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(mGoogleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(mGoogleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(getContext(),
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mLastLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(mGoogleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        addMyLocationMarker();
    }

    private void addMyLocationMarker() {
        mMap.clear();
        if (metroList.size() > 0) {
            for (Metro metro : metroList) {
                LatLng sydney = new LatLng(Double.valueOf(metro.getxCoor()), Double.valueOf(metro.getyCoor()));
                mMap.addMarker(new MarkerOptions().position(sydney).title(metro.getName()));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10.0f));
            }
        }

        final double latitude = mLastLocation.getLatitude();
        final double longitude = mLastLocation.getLongitude();
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(latitude, longitude))
                .snippet("My Location")
                .title("You"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        getMyLocation();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(getContext(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

}

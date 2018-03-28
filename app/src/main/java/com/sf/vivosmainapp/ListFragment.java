package com.sf.vivosmainapp;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Metro> metroList = new ArrayList<>();
    private MetroAdapter mAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        requestPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        getMetroFromJson();
        setRecyclerViewAdapter();

        return rootView;
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

    private void setRecyclerViewAdapter() {
        mAdapter = new MetroAdapter(getContext(), metroList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
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

    private void requestPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}

package com.sf.vivosmainapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

}

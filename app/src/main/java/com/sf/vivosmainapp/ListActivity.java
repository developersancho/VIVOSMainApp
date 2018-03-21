package com.sf.vivosmainapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Metro> metroList = new ArrayList<>();
    private MetroAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerView);

        getMetroFromJson();
        setRecyclerViewAdapter();
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
            Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MainActivity.class.getName(), "Unable to parse JSON file.", t);
        }

    }

    private void setRecyclerViewAdapter() {
        mAdapter = new MetroAdapter(getApplicationContext(), metroList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

package com.mohammadsamandari.memorableplacesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //  Defining The Variables that we need.
    static ArrayList<String> memorablePlacesNames, memorablePlacesLat, memorablePlacesLng;
    ListView memorablePlacesListView;
    static ArrayAdapter memorablePlacesArrayAdapter;
    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Defining Shared Preferences


        //  Defining the array list, giving it the first defualt item.
        memorablePlacesNames = new ArrayList<String>();
        memorablePlacesLat = new ArrayList<String>();
        memorablePlacesLng = new ArrayList<String>();

        memorablePlacesNames.add("Add a new place . . .");
        memorablePlacesLat.add("0");
        memorablePlacesLng.add("0");

        //  Defning the memorablePlacesListView and populating it with arrayList.
        memorablePlacesListView = findViewById(R.id.listview);
        memorablePlacesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, memorablePlacesNames);
        memorablePlacesListView.setAdapter(memorablePlacesArrayAdapter);

        //  listView onItemClick Function:
        memorablePlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  When an item on the listview is clicked, we have to move to the maps activity.
                //  if the first item is clicked, we are going to add a new place so there is no extra.
                //  if other items are clicked, we should have an extra to know witch on os clicked.
                //  in this case we are going to pass latitude and Longitude of the clicked item to the next activity.
                String[] location = new String[]
                        {memorablePlacesLat.get(position),
                                memorablePlacesLng.get(position),
                                memorablePlacesNames.get(position),
                                String.valueOf(position)};

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });

    }
}

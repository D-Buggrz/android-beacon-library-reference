package com.dbuggrz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.altbeacon.beaconreference.MonitoringActivity;
import org.altbeacon.beaconreference.R;

import java.util.ArrayList;
import java.util.Arrays;


public class HomeActivity extends Activity {

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    private static final String TAG = HomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_home);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );

        // Create and populate a List of planet names.
        String[] planets = new String[] { "Alan's Posts", "Employee Directory", "Conference Rooms", "Cafe Menu", "Emergency Exits"};
        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll( Arrays.asList(planets) );

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String listItemText = listAdapter.getItem(position);
                // Executed in an Activity, so 'this' is the Context
                // The fileUrl is a string URL, such as "http://www.example.com/image.png"
                if ("Conference Rooms".equalsIgnoreCase(listItemText)) {
                    Intent monitoringIntent = new Intent(view.getContext(), MonitoringActivity.class);
                    startActivity(monitoringIntent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "RESUMING THE HOME ACTIVITY");
    }

    public void onConferenceRoomsClicked(View view) {
        Log.d(TAG, "onRangingClicked - we clicked a thing.");
        Intent myIntent = new Intent(this, MonitoringActivity.class);
        this.startActivity(myIntent);
    }
}

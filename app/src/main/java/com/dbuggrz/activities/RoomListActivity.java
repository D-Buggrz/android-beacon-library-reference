package com.dbuggrz.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dbuggrz.activities.async.BeaconsListAsyncTask;
import com.dbuggrz.activities.async.BuildingDetail;
import com.dbuggrz.activities.async.LocationDetail;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beaconreference.BeaconReferenceApplication;
import org.altbeacon.beaconreference.R;
import org.altbeacon.beaconreference.RangingActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoomListActivity extends Activity implements BeaconConsumer {

    protected static final String TAG = "RoomListActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

    public static List<String> beaconUUIDs;

    public static List<LocationDetail> locations;

    public static ArrayList<String> listOfDetectedBeacons;

    public static ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        verifyBluetooth();

        listOfDetectedBeacons = new ArrayList<String>(0);
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, listOfDetectedBeacons);

        ListView listView = (ListView) findViewById(R.id.roomListView);
        listView.setAdapter(listAdapter);

        beaconUUIDs = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationDetail nextLocation = locations.get(position);
                // Executed in an Activity, so 'this' is the Context
                // The fileUrl is a string URL, such as "http://www.example.com/image.png"
                Toast toast = Toast.makeText(getApplicationContext(), nextLocation.getName() + ": " + nextLocation.getUuid(), Toast.LENGTH_SHORT);
                toast.show();

                Intent roomDetailIntent = new Intent(view.getContext(), RoomDetailActivity.class);
                roomDetailIntent.putExtra(Intent.EXTRA_TEXT, nextLocation.getUuid());
                roomDetailIntent.putExtra(Intent.EXTRA_TEXT + "_1", nextLocation.getId());
                startActivity(roomDetailIntent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
        BeaconReferenceApplication beaconReferenceApplication = ((BeaconReferenceApplication) this.getApplicationContext());
        beaconReferenceApplication.setMonitoringActivity(this);

        beaconManager.bind(this);

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        new BeaconsListAsyncTask().execute();
        Log.i(TAG, "Starting the ranging activity.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void onRangingClicked(View view) {
        Log.d(TAG, "onRangingClicked - we clicked a thing.");
        Intent myIntent = new Intent(this, RangingActivity.class);
        this.startActivity(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        BeaconReferenceApplication beaconReferenceApplication = ((BeaconReferenceApplication) this.getApplicationContext());
        beaconReferenceApplication.setMonitoringActivity(this);

        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
        beaconManager.unbind(this);
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
        beaconManager.unbind(this);
    }

    private void verifyBluetooth() {
        Log.i(TAG, "checking for bluetooth");
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    public void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, line);
            }
        });
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "We have a service connection - Monitoring activity service connect. ");
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    boolean foundANewBeacon = false;
                    for (Beacon nextBeacon : beacons) {
                        String beaconUUIDString = nextBeacon.getId1().toString();
                        Log.d(TAG, "Found a beacon - " + beaconUUIDString +
                                " address: " + nextBeacon.getBluetoothAddress() +
                                ", name: " + nextBeacon.getBluetoothName() +
                                ", distance: " + nextBeacon.getDistance() +
                                ", service uuid: " + nextBeacon.getServiceUuid() +
                                ", string: " + nextBeacon.toString());
                        if (!beaconUUIDs.contains(beaconUUIDString)) {
                            beaconUUIDs.add(beaconUUIDString);
                        }
                    }
                    addBeaconsToList();
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public void addBeaconsToList() {
        Log.d(TAG, "Looking for the currently detected beacons. " + (beaconUUIDs == null ? "null" : beaconUUIDs.size()));
        if (locations == null) {
            return;
        }
        for (int i = 0; i < locations.size(); i++) {
            boolean datasetChanged = false;
            if (beaconUUIDs.contains(locations.get(i).getUuid()) && !(locations.get(i) instanceof BuildingDetail)) {
                Log.d(TAG, "Updating the beacon with beacon found. ");
                String name = listOfDetectedBeacons.get(i);
                if (!name.startsWith("(*)")) {
                    Log.d(TAG, "The dataset has been changed. ");
                    datasetChanged = true;
                    listOfDetectedBeacons.set(i, "(*) " + locations.get(i).getName());
                }
            }
            if (datasetChanged) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}

package com.dbuggrz.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbuggrz.activities.async.BeaconsDetailAsyncTask;
import com.dbuggrz.activities.async.LocationDetail;
import com.dbuggrz.activities.async.RoomDetail;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beaconreference.R;

import java.util.Collection;

public class RoomDetailActivity extends Activity implements BeaconConsumer {

    private static final String TAG = RoomDetailActivity.class.getName();
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    private String uuid;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        beaconManager.bind(this);

        this.uuid = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        this.id = getIntent().getStringExtra(Intent.EXTRA_TEXT + "_1");
        Log.i(TAG, "Starting the ranging activity for uuid " + uuid + " and id = " + id);
        BeaconsDetailAsyncTask beaconsDetailAsyncTask = new BeaconsDetailAsyncTask();
        beaconsDetailAsyncTask.setRoomDetailActivity(this);
        /*This will update the page using the UUID. */
        beaconsDetailAsyncTask.execute(this.id);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "we have a service connection.");
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                boolean foundBeacon = false;
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    Beacon firstBeacon = beacons.iterator().next();
                    Log.i(TAG, "The first beacon " + firstBeacon.getId1() + " is about " + firstBeacon.getDistance() + " meters away." + " and might not match " + uuid);
                    for (Beacon nextBeacon : beacons) {
                        String beaconId = nextBeacon.getId1().toString();
                        if (uuid.equalsIgnoreCase(beaconId)) {
                            Log.i(TAG, "Updating the range for this bad boy");
                            foundBeacon = true;
                            updateDistance(nextBeacon.getDistance());
                        }
                    }
                }
                if (!foundBeacon) {
                    updateDistance(-1d);
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    public void updateRoomDetails(final LocationDetail roomDetail) {
        if (!(roomDetail instanceof RoomDetail)) {
            throw new ClassCastException("Cannot update the room detail screen " +
                    " with anything other than a RoomDetail.class : " + roomDetail.getClass());
        }
        runOnUiThread(new Runnable() {
            public void run() {
                Log.i(TAG, "updating room detail with " + roomDetail.getUuid());
                ((TextView) findViewById(R.id.locationName)).setText(roomDetail.getName());
                ((TextView) findViewById(R.id.descriptionText)).setText(roomDetail.getDescription());
                ((TextView) findViewById(R.id.agendaText)).setText(((RoomDetail) roomDetail).getMeetingAgenda());
            }
        });
    }

    /**
     * Updates the distance on the UI
     * @param distance
     */
    private void updateDistance(final Double distance) {
        final String distanceString = distance > 0 ? String.format( "%.2f meters away", distance ) : "Out of Range";

        runOnUiThread(new Runnable() {
            public void run() {
                TextView distanceView = (TextView) findViewById(R.id.distance);
                distanceView.setText(distanceString);
                TextView agendaLabel = (TextView) findViewById(R.id.agendaLabel);
                TextView agenda = (TextView) findViewById(R.id.agendaText);
                ImageView imageView = (ImageView) findViewById(R.id.mapImg);
                if (distance > 2) {
                    agendaLabel.setVisibility(View.GONE);
                    agenda.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else if (distance < 0) {
                    agendaLabel.setVisibility(View.GONE);
                    agenda.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    distanceView.setText("You are here!");
                    agenda.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
            }
        });
    }
}

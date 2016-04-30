package com.dbuggrz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dbuggrz.activities.async.BeaconsDetailAsyncTask;
import com.dbuggrz.activities.async.LocationDetail;
import com.dbuggrz.activities.async.RoomDetail;

import org.altbeacon.beaconreference.R;

public class RoomDetailActivity extends Activity {

    private static final String TAG = RoomDetailActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        String uuid = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        BeaconsDetailAsyncTask beaconsDetailAsyncTask = new BeaconsDetailAsyncTask();
        beaconsDetailAsyncTask.setRoomDetailActivity(this);
        /*This will update the page using the UUID. */
        beaconsDetailAsyncTask.execute(uuid);
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
                ((TextView) findViewById(R.id.agendaLabel)).setText(((RoomDetail) roomDetail).getMeetingAgenda());
            }
        });
    }
}

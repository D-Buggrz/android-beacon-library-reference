package com.dbuggrz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.altbeacon.beaconreference.R;

public class RoomDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        String uuid = getIntent().getStringExtra(Intent.EXTRA_TEXT);
    }
}

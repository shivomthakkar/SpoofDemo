package com.quicsolv.qsspoofer;

import android.bluetooth.le.AdvertiseSettings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uriio.beacons.Beacons;
import com.uriio.beacons.model.iBeacon;

public class AdvertisingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising);

        Beacons.initialize(this);

        BluetoothResponse response = (BluetoothResponse) getIntent().getSerializableExtra("BLEObject");
        new iBeacon(AppUtils.hexToByte(response.getUuid()), 100, Integer.parseInt(response.getMinor()), AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY, AdvertiseSettings.ADVERTISE_TX_POWER_HIGH, response.getFlags(), response.getDeviceName()).start();
    }
}

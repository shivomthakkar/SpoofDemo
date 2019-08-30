package com.quicsolv.qsspoofer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import static com.quicsolv.qsspoofer.AppUtils.bytesToHex;


public class MainActivity extends AppCompatActivity {


    private final int REQUEST_ENABLE_BT = 101;
    BluetoothAdapter bluetoothAdapter;
    ListView lVBluetoothDevices;

    ArrayList<BluetoothResponse> deviceList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lVBluetoothDevices = findViewById(R.id.lVBluetoothDevices);
        lVBluetoothDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AdvertisingActivity.class);
                intent.putExtra("BLEObject", deviceList.get(position));
                startActivity(intent);
            }
        });

        Dexter.withActivity(this).withPermissions(Manifest.permission.BLUETOOTH, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null || !bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        }

        BluetoothLeScanner leScanner = bluetoothAdapter.getBluetoothLeScanner();
        leScanner.startScan(leScanCallback);

    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            List<ExtractedDataResponse> extractedData = AppUtils.extractData(bytesToHex(result.getScanRecord().getBytes()));

            BluetoothResponse response = new BluetoothResponse();
            response.setDeviceName(result.getDevice().getName());
            response.setTxPowerLevel(result.getTxPower());
            response.setRssi(result.getRssi());


            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5) {
                if (    ((int) result.getScanRecord().getBytes()[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) result.getScanRecord().getBytes()[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(result.getScanRecord().getBytes(), startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //Here is your UUID
                String uuid = hexString.substring(0, 8) + "-" +
                        hexString.substring(8, 12) + "-" +
                        hexString.substring(12, 16) + "-" +
                        hexString.substring(16, 20) + "-" +
                        hexString.substring(20, 32);

                //Here is your Major value
                int major = (result.getScanRecord().getBytes()[startByte + 20] & 0xff) * 0x100 + (result.getScanRecord().getBytes()[startByte + 21] & 0xff);
                //Here is your Minor value
                final int minor = (result.getScanRecord().getBytes()[startByte + 22] & 0xff) * 0x100 + (result.getScanRecord().getBytes()[startByte + 23] & 0xff);

                response.setUuid(uuid);
                response.setMajor(String.valueOf(major));
                response.setMinor(String.valueOf(minor));


                boolean isInList = false;
                for (BluetoothResponse oneDevice: deviceList) {
                    if (oneDevice.getUuid().equals(response.getUuid()) && oneDevice.getMajor().equals(response.getMajor()) && oneDevice.getMinor().equals(response.getMinor()))
                        isInList = true;
                }
                if (!isInList)
                    deviceList.add(response);
                ArrayAdapter adapter = new BluetoothItemAdapter(MainActivity.this, deviceList);
                lVBluetoothDevices.setAdapter(adapter);

            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
}

package com.quicsolv.qsspoofer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BluetoothItemAdapter extends ArrayAdapter<BluetoothResponse> {

    private final Context context;
    private final List<BluetoothResponse> devices;


    public BluetoothItemAdapter(Context context, List<BluetoothResponse> devices) {
        super(context, -1,  devices);
        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_bluetooth_device, parent, false);

        TextView tVUUID = rowView.findViewById(R.id.tVUUID);
        tVUUID.setText(devices.get(position).getDeviceName() + "");
        TextView tVMajor = rowView.findViewById(R.id.tVMajor);
        tVMajor.setText(devices.get(position).getMajor());
        TextView tVMinor = rowView.findViewById(R.id.tVMinor);
        tVMinor.setText(devices.get(position).getMinor());

        return rowView;
    }

}

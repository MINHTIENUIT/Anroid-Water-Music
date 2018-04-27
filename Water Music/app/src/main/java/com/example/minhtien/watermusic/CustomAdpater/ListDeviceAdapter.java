package com.example.minhtien.watermusic.CustomAdpater;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.minhtien.watermusic.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minh Tien on 05/04/2018.
 */

public class ListDeviceAdapter extends ArrayAdapter<BluetoothDevice>{
    private Activity context;

    public ListDeviceAdapter(@NonNull Activity context, int resource, @NonNull List<BluetoothDevice> objects) {
        super(context, resource, objects);
        this.context = context;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_device,null,false);
        }

        BluetoothDevice device = getItem(position);
        TextView tvName = convertView.findViewById(R.id.txt_name);
        TextView tvAddr = convertView.findViewById(R.id.txt_addr);
        if (device.getName() != null){
            tvName.setText(device.getName());
        }else{
            tvName.setText("");
        }

        tvAddr.setText(device.getAddress());
        return convertView;
    }
}

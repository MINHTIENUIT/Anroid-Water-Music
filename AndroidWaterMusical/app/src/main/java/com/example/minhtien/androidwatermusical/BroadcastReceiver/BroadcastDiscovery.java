package com.example.minhtien.androidwatermusical.BroadcastReceiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Minh Tien on 05/04/2018.
 */

public class BroadcastDiscovery extends BroadcastReceiver {
    private final String TAG = "StateActionFound";
    ActionFound actionFound;

    public BroadcastDiscovery(ActionFound action) {
        actionFound = action;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_FOUND)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            actionFound.action(device);
            Log.d(TAG, "onReceive: ");
        }
    }

    public interface ActionFound{
        void action(BluetoothDevice device);
    }
}
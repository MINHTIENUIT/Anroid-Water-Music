package com.example.minhtien.watermusic.BroadcastReceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Minh Tien on 05/04/2018.
 */

public class BroadcastEnable extends BroadcastReceiver {
    private final String TAG = "StateEnable";
    Context mcontext;
    private InterfaceEnable mInterfaceEnable;

    public BroadcastEnable(InterfaceEnable interfaceEnable) {
        mInterfaceEnable = interfaceEnable;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "onReceive: Bluetooth Turned On");
                    mInterfaceEnable.start();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "onReceive: Bluetooth Turned Off");
                    mInterfaceEnable.stop();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d(TAG, "onReceive: Bluetooth Turning On");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d(TAG, "onReceive: Bluetooth Turning Off");
                    break;
            }
        }
    }

    public interface InterfaceEnable {
        public void start();
        public void stop();
    }
}
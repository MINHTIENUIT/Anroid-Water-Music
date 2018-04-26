package com.example.minhtien.androidwatermusical.BroadcastReceiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Minh Tien on 06/04/2018.
 */

public class BroadcastPaired extends BroadcastReceiver {
    ActionPaired actionPaired;

    public BroadcastPaired(ActionPaired actionPaired){
        this.actionPaired = actionPaired;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
            BluetoothDevice mdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (mdevice.getBondState() == BluetoothDevice.BOND_BONDED){
                actionPaired.action(mdevice);
            }
        }
    }

    public interface ActionPaired{
        public void action(BluetoothDevice device);
    }
}

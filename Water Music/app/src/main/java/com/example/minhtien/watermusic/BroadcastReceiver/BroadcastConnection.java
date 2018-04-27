package com.example.minhtien.watermusic.BroadcastReceiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created by Minh Tien on 13/04/2018.
 */

public class BroadcastConnection extends BroadcastReceiver implements Serializable{

    ActionConnect actionConnect;

    public BroadcastConnection(ActionConnect actionConnect){
        this.actionConnect = actionConnect;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            actionConnect.actionConnected(device);
        }
        if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
            Toast.makeText(context, "Disconneted", Toast.LENGTH_SHORT).show();
            actionConnect.actionDisconnected();
        }
    }

    public interface ActionConnect{
        public void actionConnected(BluetoothDevice device);
        public void actionDisconnected();
    }
}

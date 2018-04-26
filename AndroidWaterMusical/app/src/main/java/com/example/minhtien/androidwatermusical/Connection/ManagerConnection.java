package com.example.minhtien.androidwatermusical.Connection;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

/**
 * Created by Minh Tien on 13/04/2018.
 */

public class ManagerConnection {
    private static ManagerConnection managerConnection = null;
    private static ConnectedThread connectedThread = null;
    private static ConnectThread connectThread = null;

    public static ManagerConnection getInstance(BluetoothDevice device) {
        if (managerConnection == null) {
            managerConnection = new ManagerConnection();
        }

        if (connectThread == null) {
            connectThread = new ConnectThread(device);
            if (connectedThread == null) {
                connectedThread = new ConnectedThread(connectThread.getSocket());
            }
        }

        return managerConnection;
    }

    public ConnectThread getConnectThread() {
        return connectThread;
    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }

    public void connect() {
        if (connectThread != null) {
            connectThread.start();
        }
    }


    public void start() {
        if (connectedThread != null) {
            connectedThread.start();
        }
    }

    public void send(String input) {
        if (connectedThread != null) {
            connectedThread.write(input);
        }
    }

    public void cancel() {
        if (connectThread != null) {
            connectThread.cancel();
        }
        connectThread = null;
        if (connectedThread != null) {
            connectedThread.cancel();
        }
        connectedThread = null;

    }

    public void setHandler(Handler handler){
        connectedThread.setHandler(handler);
    }
}

package com.example.minhtien.watermusic.Connection;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

/**
 * Created by Minh Tien on 13/04/2018.
 */

public class ManagerConnection {
    private static ManagerConnection managerConnection = null;
    private static ConnectedThread connectedThread = null;
    private static ConnectThread connectThread = null;
    private static boolean connected = false;

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
            connected = true;
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

    public static void cancel() {
        if (connectThread != null) {
            connectThread.cancel();
        }
        connectThread = null;
        if (connectedThread != null) {
            connectedThread.cancel();
        }
        connectedThread = null;
        connected = false;
    }

    public static boolean isConnected(){
        return connected;
    }

    public void setHandler(Handler handler){
        connectedThread.setHandler(handler);
    }
}

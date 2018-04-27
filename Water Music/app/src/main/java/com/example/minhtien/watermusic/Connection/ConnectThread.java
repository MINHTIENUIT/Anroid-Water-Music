package com.example.minhtien.watermusic.Connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Minh Tien on 13/04/2018.
 */

public class ConnectThread extends Thread{

    public final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "connect";
    private final BluetoothDevice mDevice;
    private static BluetoothSocket mSocket;

    ConnectThread(BluetoothDevice device){
        mDevice = device;

        BluetoothSocket tmp = null;
        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);
        } catch (IOException e) {
            Log.e(TAG, "ConnectThread: Socket's create() method failed", e);
        }
        mSocket = tmp;
    }

    @Override
    public void run() {
        try {
            mSocket.connect();
        }catch (IOException e){
            try {
                mSocket.close();
            } catch (IOException e1) {
                Log.d(TAG, "Could not close the client socket", e1);
            }
            Log.e(TAG, "run: Error connect", e);
            return;
        }
        Log.d(TAG, "run: Connected");
    }

    public BluetoothSocket getSocket(){
        return mSocket;
    }

    void cancel(){
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

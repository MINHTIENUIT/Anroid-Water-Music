package com.example.minhtien.androidwatermusical.Connection;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Minh Tien on 13/04/2018.
 */

public class ConnectedThread extends Thread {

    private Handler mHandler;

    private final static String TAG = "connected";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer;

    ConnectedThread(BluetoothSocket socket) {
        this.mmSocket = socket;
        InputStream tmpIn  = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void setHandler(Handler handler){
        mHandler = handler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        Log.d(TAG, "run: lkjalsdf");
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.available();
                if(bytes != 0) {
                    buffer = new byte[1024];
                    SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                    bytes = mmInStream.available(); // how many bytes are ready to be read?
                    bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                    mHandler.obtainMessage(0, bytes, -1, buffer)
                            .sendToTarget(); // Send the obtained bytes to the UI activity
                    String readMessage = new String(String.valueOf(bytes));

                }
            } catch (IOException e) {
                e.printStackTrace();

                break;
            }

        }
    }

    public void write(String input){
        byte[] bytes = input.getBytes();
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Handler getHandler(){
        return mHandler;
    }

    public void cancel(){
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
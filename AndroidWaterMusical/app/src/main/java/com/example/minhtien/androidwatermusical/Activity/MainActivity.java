package com.example.minhtien.androidwatermusical.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.minhtien.androidwatermusical.BroadcastReceiver.BroadcastEnable;
import com.example.minhtien.androidwatermusical.R;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 0;

    private Button btnTurnOn;
    private BluetoothAdapter mBTAdapter;
    private BroadcastEnable mBREnable= new BroadcastEnable(new BroadcastEnable.InterfaceEnable() {
        @Override
        public void start() {
            enable();
        }

        @Override
        public void stop() {

        }
    });;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        btnTurnOn = findViewById(R.id.btn_turn_on);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBREnable,intentFilter);

        if (mBTAdapter == null){
            finish();
        }

        if (mBTAdapter.isEnabled()){
            enable();
        }

        btnTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBTAdapter.isEnabled())
                {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }else {
                    enable();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBREnable);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Request Permission");
                    builder.setMessage("Permission Location denied.\nAre you allow this again?");
                    builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermission();
                        }
                    });
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }
        }
    }

    public void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    public void enable(){
        Intent intent = new Intent(MainActivity.this,DevicesActivity.class);
        startActivity(intent);
        //btnTurnOn.setText("TURN OFF");
    }
}
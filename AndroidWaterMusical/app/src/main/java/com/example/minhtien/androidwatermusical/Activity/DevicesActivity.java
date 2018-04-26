package com.example.minhtien.androidwatermusical.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.minhtien.androidwatermusical.BroadcastReceiver.BroadcastConnection;
import com.example.minhtien.androidwatermusical.BroadcastReceiver.BroadcastDiscovery;
import com.example.minhtien.androidwatermusical.BroadcastReceiver.BroadcastPaired;
import com.example.minhtien.androidwatermusical.Connection.ManagerConnection;
import com.example.minhtien.androidwatermusical.CustomAdpater.ListDeviceAdapter;
import com.example.minhtien.androidwatermusical.R;

import java.util.ArrayList;
import java.util.Set;

public class DevicesActivity extends Activity {
    final Context context = this;

    private ArrayList<BluetoothDevice> listPaired;
    private ListDeviceAdapter adapterPaired;

    ArrayList<BluetoothDevice> listDeviceFound;
    ListDeviceAdapter adapterFound;
    ListView lvPaired;
    TextView tvNothingPaired;

    TextView tvState;
    ListView lvFound;

    Button btnScan;

    private BluetoothAdapter mBTAdapter;

    private static BluetoothDevice device;

    boolean checkRigisterConnection = false;

    final BroadcastConnection broadcastConnection = new BroadcastConnection(new BroadcastConnection.ActionConnect() {
        @Override
        public void actionConnected(BluetoothDevice device) {
            Intent intent = new Intent(context,ControllerActivity.class);
            intent.putExtra("device",device.getAddress());
            startActivity(intent);
        }

        @Override
        public void actionDisconnected() {

        }
    });

    final BroadcastDiscovery broadcastDiscovery = new BroadcastDiscovery(new BroadcastDiscovery.ActionFound() {
        @Override
        public void action(BluetoothDevice device) {
            listDeviceFound.add(device);
            tvState.setText(listDeviceFound.size() + " Device(s) Found");
            adapterFound.notifyDataSetChanged();
        }
    });
    final BroadcastPaired mBRPaired = new BroadcastPaired(new BroadcastPaired.ActionPaired() {
        @Override
        public void action(BluetoothDevice device) {
            listPaired.add(device);
            adapterPaired.notifyDataSetChanged();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        initView();
        initBroadcast();
        getDevicePared();
        setEvent();
    }

    //Install View
    public void initView() {
        lvPaired = findViewById(R.id.lv_paired);
        btnScan = findViewById(R.id.btn_scan);

        listPaired = new ArrayList<>();
        adapterPaired = new ListDeviceAdapter(this, R.layout.custom_item_device, listPaired);

        listDeviceFound = new ArrayList<>();
        adapterFound = new ListDeviceAdapter((Activity) context, R.layout.custom_item_device, listDeviceFound);

        tvNothingPaired = findViewById(R.id.tv_nothing_paired);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        lvPaired.setAdapter(adapterPaired);
    }

    //Install BroadcastService
    public void initBroadcast(){

        IntentFilter intentFilterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastDiscovery, intentFilterFound);

        IntentFilter intentFilterPaired = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBRPaired, intentFilterPaired);

    }

    public void initDialog() {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.discovery_fragment, null);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        dialog.setTitle("Find Devices");


        tvState = dialog.findViewById(R.id.tv_state);
        lvFound = dialog.findViewById(R.id.lv_device_found);

        ToggleButton btnScanDevice = dialog.findViewById(R.id.btn_scan);
        Button btnCancel = dialog.findViewById(R.id.btn_stop);

        lvFound.setAdapter(adapterFound);

        btnScanDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listDeviceFound.clear();
                    mBTAdapter.startDiscovery();
                } else {
                    mBTAdapter.cancelDiscovery();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBTAdapter.isDiscovering()){
                    mBTAdapter.cancelDiscovery();
                }
                dialog.dismiss();
            }
        });

        lvFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = listDeviceFound.get(i);
                device.createBond();
                if (listPaired.size() == 0) tvNothingPaired.setVisibility(View.GONE);
            }
        });
        dialog.show();

    }

    public void setEvent() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialog();
            }
        });

        lvPaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentFilter intentFilterConnection = new IntentFilter();
                intentFilterConnection.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                intentFilterConnection.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                registerReceiver(broadcastConnection,intentFilterConnection);
                checkRigisterConnection = true;
                device = mBTAdapter.getRemoteDevice(listPaired.get(i).getAddress());
                ManagerConnection.getInstance(device).connect();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (checkRigisterConnection)
            unregisterReceiver(broadcastConnection);
        unregisterReceiver(mBRPaired);
        unregisterReceiver(broadcastDiscovery);
        if (device!=null){
            ManagerConnection.getInstance(device).cancel();
        }
        Log.d("abc", "onDestroy: called");
        super.onDestroy();
    }

    public void getDevicePared() {
        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            tvNothingPaired.setVisibility(View.GONE);
            for (BluetoothDevice device : pairedDevices) {
                Log.d("paired", "getDevicePared address: " + device.getAddress());
                listPaired.add(device);
                adapterPaired.notifyDataSetChanged();
            }
            Log.d("paired", "getDevicePared on list: " + listPaired.get(0).getAddress());
        }
    }
}
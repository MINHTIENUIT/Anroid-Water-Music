package com.example.minhtien.watermusic;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.minhtien.watermusic.BroadcastReceiver.BroadcastConnection;
import com.example.minhtien.watermusic.BroadcastReceiver.BroadcastDiscovery;
import com.example.minhtien.watermusic.BroadcastReceiver.BroadcastEnable;
import com.example.minhtien.watermusic.BroadcastReceiver.BroadcastPaired;
import com.example.minhtien.watermusic.Connection.ManagerConnection;
import com.example.minhtien.watermusic.CustomAdpater.ListDeviceAdapter;

import java.util.ArrayList;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevicesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private BroadcastConnection mBroadcastConnection;
    ProgressDialog pd;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

    public DevicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param broadcastConnection Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevicesFragment newInstance(BroadcastConnection broadcastConnection, String param2) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, broadcastConnection);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBroadcastConnection = (BroadcastConnection) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pd = new ProgressDialog(getContext());
        initBroadcast();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter.isEnabled()){
            getDevicePared();
        }
        setEvent();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionDevices(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteractionDevices(Uri uri);
    }

    public void initView(View view) {

        listPaired = new ArrayList<>();
        adapterPaired = new ListDeviceAdapter((Activity) getContext(), R.layout.custom_item_device, listPaired);

        listDeviceFound = new ArrayList<>();
        adapterFound = new ListDeviceAdapter((Activity) getContext(), R.layout.custom_item_device, listDeviceFound);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        lvPaired = view.findViewById(R.id.lv_paired);
        lvPaired.setAdapter(adapterPaired);
        btnScan = view.findViewById(R.id.btn_scan);
        tvNothingPaired = view.findViewById(R.id.tv_nothing_paired);
    }

    public void initBroadcast(){

        IntentFilter intentFilterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(broadcastDiscovery, intentFilterFound);

        IntentFilter intentFilterPaired = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getContext().registerReceiver(mBRPaired, intentFilterPaired);

    }

    public void initDialog() {
        View view;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.discovery_fragment, null);

        final Dialog dialog = new Dialog(getContext());
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
                ManagerConnection.cancel();
                pd = new ProgressDialog(getContext());
                pd.setTitle("Connecting");
                pd.setMessage("Please wait");
                IntentFilter intentFilterConnection = new IntentFilter();
                intentFilterConnection.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                intentFilterConnection.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                getContext().registerReceiver(mBroadcastConnection,intentFilterConnection);
                checkRigisterConnection = true;
                device = mBTAdapter.getRemoteDevice(listPaired.get(i).getAddress());
                ManagerConnection.getInstance(device).connect();
                pd.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mBRPaired);
        getContext().unregisterReceiver(broadcastDiscovery);
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

    public void clearDevicePared(){
        listPaired.clear();
        adapterPaired.notifyDataSetChanged();
    }

    public void dismiss(){
        pd.dismiss();
    }

    public BluetoothDevice getDevice(){
        return device;
    }
}
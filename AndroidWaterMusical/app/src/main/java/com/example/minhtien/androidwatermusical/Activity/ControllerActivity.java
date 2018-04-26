package com.example.minhtien.androidwatermusical.Activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.minhtien.androidwatermusical.Connection.ManagerConnection;
import com.example.minhtien.androidwatermusical.R;
import com.example.minhtien.androidwatermusical.SpectrumFragment;
import com.example.minhtien.androidwatermusical.untils;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Arrays;

public class ControllerActivity extends AppCompatActivity {
    static final BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();

    private TextView tvName;
    private ImageButton btnPrev;
    private ImageButton btnPause;
    private ImageButton btnNext;
    static BluetoothDevice device;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private SpectrumFragment spectrumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        initView();
        initEvent();

        Intent intent = getIntent();
        String address = intent.getStringExtra("device");
        device = mBTAdapter.getRemoteDevice(address);

        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0){
                    String readMessage = null;
                    try{
                        readMessage = new String((byte[]) msg.obj,"UTF-8");
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(Character.MIN_VALUE);
                        readMessage = readMessage.replaceAll("[\uFEFF-\uFFFF]", "");
                        Log.d("test", "handleMessage: "+readMessage);
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    String[] strings = readMessage.split(":");

                    if (strings.length != 0){

                        switch (strings[0]){
                            case "Name":
                                tvName.setText(strings[1]);
                                break;
                            case "SP":
                                spectrumFragment.setSpectrum(arraySpectrum(strings));
                                break;
                        }
                    }
                }
            }
        };

        ManagerConnection.getInstance(device).setHandler(handler);

        ManagerConnection.getInstance(device).start();
    }

    public void initView(){
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnPause = (ImageButton) findViewById(R.id.btn_pause);
        btnPrev = (ImageButton) findViewById(R.id.btn_prev);
        tvName = (TextView) findViewById(R.id.tv_name_song);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        spectrumFragment = new SpectrumFragment();

        fragmentTransaction.replace(R.id.fragment_spectrum,spectrumFragment);
        fragmentTransaction.commit();
    }

    public void initEvent(){
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("prev");
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("pause");
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("next");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ManagerConnection.getInstance(device).cancel();
    }

    public ArrayList<Integer> arraySpectrum(String[] strings){
        ArrayList<Integer> integers = new ArrayList<>();
        for (String s:strings){
            StringBuffer buffer = new StringBuffer();
            if (!s.equals("SP")){
                for (int i = 0; i<s.length();i++){
                    if (s.charAt(i)<58 && s.charAt(i) >47){
                        buffer.append(s.charAt(i));
                    };
                }
                try {
                    integers.add(Integer.valueOf(buffer.toString()));
                }catch (Exception e){
                    integers.add(0);
                }
            }
        }
        return integers;
    }
}
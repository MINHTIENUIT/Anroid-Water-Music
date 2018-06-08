package com.example.minhtien.watermusic;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minhtien.watermusic.Connection.ManagerConnection;
import com.example.minhtien.watermusic.Model.MessageWM;
import com.example.minhtien.watermusic.Model.Song;
import com.google.gson.Gson;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ControllerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class ControllerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private TextView tvName;
    private ImageButton btnPrev;
    private ImageButton btnPause;
    private ImageButton btnNext;
    static BluetoothDevice device;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private SpectrumFragment spectrumFragment;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @SuppressLint("ValidFragment")
    public ControllerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControllerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControllerFragment newInstance(String param1, String param2) {
        ControllerFragment fragment = new ControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mParam1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_controller, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0){
                    String readMessage = null;
                    try{
                        readMessage = new String((byte[]) msg.obj,"UTF-8").trim();
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    getResponse(readMessage);

                }
            }
        };
        ManagerConnection.getInstance(device).setHandler(handler);
        ManagerConnection.getInstance(device).start();

        initEvent();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionController(uri);
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
        void onFragmentInteractionController(Uri uri);
    }

    public void initView(View view){
        btnNext = (ImageButton) view.findViewById(R.id.btn_next);
        btnPause = (ImageButton) view.findViewById(R.id.btn_pause);
        btnPrev = (ImageButton) view.findViewById(R.id.btn_prev);
        tvName = (TextView) view.findViewById(R.id.tv_name_song);
        tvName.setSelected(true);

        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        spectrumFragment = new SpectrumFragment();

        fragmentTransaction.replace(R.id.fragment_spectrum,spectrumFragment);
        fragmentTransaction.commit();
    }

    public void initEvent(){
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("C:res");
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("C:pse");
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("C:nxt");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    void getResponse(String message){
        MessageWM messageWM = null;
        try {
            messageWM = new Gson().fromJson(message,MessageWM.class);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }

        if (messageWM == null){
            return;
        }

        switch (messageWM.getType()){
            case "Name":
                try{
                    Song song = new Gson().fromJson(messageWM.getMessage(),Song.class);
                    tvName.setText(song.getName().replace(".wav",""));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case "Spectrum":

                default:
                    Log.d("error", "getResponse: " + messageWM.getMessage());
                    Toast.makeText(getContext(), messageWM.getMessage(), Toast.LENGTH_LONG).show();
                    break;
        }

    }
}

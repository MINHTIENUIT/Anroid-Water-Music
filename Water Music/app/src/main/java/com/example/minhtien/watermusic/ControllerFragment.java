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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minhtien.watermusic.Connection.ManagerConnection;
import com.example.minhtien.watermusic.CustomAdpater.SongAdapter;
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
    private ImageButton btnSync;
    private ImageButton btnVolume;

    private RecyclerView mRecyclerView;
    private SongAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    static BluetoothDevice device;
    private ArrayList<Song> listSong;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String statePlaying = "PLAY";

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
        btnSync = view.findViewById(R.id.btn_sync);
        btnVolume = view.findViewById(R.id.btn_volume);
        tvName = (TextView) view.findViewById(R.id.tv_name_song);
        tvName.setSelected(true);

        listSong = new ArrayList<Song>();

        mRecyclerView = view.findViewById(R.id.rv_list_song);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SongAdapter(listSong);

        mRecyclerView.setAdapter(mAdapter);
    }

    public void initEvent(){
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("C:prv");
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statePlaying.equals("PLAY"))
                    ManagerConnection.getInstance(device).send("C:pse");
                else{
                    ManagerConnection.getInstance(device).send("C:res");
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("C:nxt");
            }
        });

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listSong.clear();
                ManagerConnection.getInstance(device).send("C:syn");
                Animation rotation = AnimationUtils.loadAnimation(getContext(),R.anim.rotation);
                rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);

                btnSync.startAnimation(rotation);
            }
        });

        btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerConnection.getInstance(device).send("C:vol");
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
        Log.d("test", "getResponse: " + message);
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
                    int index = -1;
                    for (int i = 0; i < listSong.size(); i++){
                        if (song.getName().equals(listSong.get(i).getName())){
                            index = i;
                            break;
                        };
                    }
                    mAdapter.setIndex(index);
                    mAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case "Spectrum":
                break;
            case "Control":
                if (messageWM.getMessage().equals("pause")){
                    btnPause.setImageResource(R.drawable.ic_play);
                    statePlaying = "PAUSE";
                }
                if (messageWM.getMessage().equals("resume")){
                    btnPause.setImageResource(R.drawable.ic_pause_black_24dp);
                    statePlaying = "PLAY";
                }
                break;
            case "Sync":
                if (messageWM.getMessage().equals("sync completed"))
                    btnSync.clearAnimation();

                break;
            case "List":
                listSong.add(new Song(messageWM.getMessage()));
                mAdapter.notifyDataSetChanged();
                break;
                default:
                    Log.d("error", "getResponse: " + messageWM.getMessage());
                    Toast.makeText(getContext(), messageWM.getMessage(), Toast.LENGTH_LONG).show();
                    break;
        }

    }
}

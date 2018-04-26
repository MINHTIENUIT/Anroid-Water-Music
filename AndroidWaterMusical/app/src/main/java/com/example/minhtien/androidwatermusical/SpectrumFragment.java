package com.example.minhtien.androidwatermusical;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Minh Tien on 26/04/2018.
 */

public class SpectrumFragment extends Fragment {
    ProgressBar[] progressBars = new ProgressBar[8];
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spectrum_view,container,false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (int i = 0; i< progressBars.length; i++){
            String progressBarID = "pb_" + (i+1);
            int resID = getResources().getIdentifier(progressBarID,"id",view.getContext().getPackageName());

            progressBars[i] = (ProgressBar) view.findViewById(resID);
        }
    }

    public void setSpectrum(ArrayList<Integer> progress){
        for (int i = 0; i<progressBars.length;i++){
            progressBars[i].setProgress(progress.get(i));
        }
        //Pb1.notify();
    }
}

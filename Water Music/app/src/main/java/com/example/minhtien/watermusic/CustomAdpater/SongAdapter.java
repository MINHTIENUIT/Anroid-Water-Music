package com.example.minhtien.watermusic.CustomAdpater;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.minhtien.watermusic.Model.Song;
import com.example.minhtien.watermusic.R;

import java.util.ArrayList;

/**
 * Created by Tan on 6/15/2018.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    ArrayList<Song> mDataset;
    int index;

    public SongAdapter(ArrayList<Song> listSong) {
        mDataset = listSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_song, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mDataset.get(position));
        if (index == position) {
            holder.setCurrentSong(true);
        }else{
            holder.setCurrentSong(false);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameSong;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameSong = itemView.findViewById(R.id.tv_name_song_custom);
        }

        public void bindData(Song song) {
            mNameSong.setText(song.getName().replace(".wav", ""));
        }

        public void setCurrentSong(boolean current) {
            if (current)
                mNameSong.setTextColor(Color.GREEN);
            else
                mNameSong.setTextColor(Color.WHITE);
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

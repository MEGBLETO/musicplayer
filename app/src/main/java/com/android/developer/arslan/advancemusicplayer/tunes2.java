package com.android.developer.arslan.advancemusicplayer;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import Model.Song;
import Request.SoundcloudApiReq;

public class tunes2 extends PlayerActivity {

    public void getSongList(){
        RequestQueue queue = volleySingleton.getInstance(this).getmRequestQueue();

        SoundcloudApiReq request = new SoundcloudApiReq(queue);

        request.getSongList(new SoundcloudApiReq.DeezerInterface() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                getSongList();
            }

            @Override
            public void onError(String message) {

            }
        });
    }

}


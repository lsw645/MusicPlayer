package com.lxw.musicplayer.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.BaseActivity;
import com.lxw.musicplayer.data.model.Folder;

public class PlayListDetailsActivity extends BaseActivity {
    private static  final String EXTRA_FOLDER="folder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_details);
    }


    public static Intent  launchIntentForFolder(Context context, Folder folder){
        Intent intent=new Intent();
        intent.putExtra(EXTRA_FOLDER,folder);
        intent.setClass(context,PlayListDetailsActivity.class);
        return  intent;
    }
}

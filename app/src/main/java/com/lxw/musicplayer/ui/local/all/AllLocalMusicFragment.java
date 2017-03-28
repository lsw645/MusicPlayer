package com.lxw.musicplayer.ui.local.all;

import android.os.Bundle;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.BaseFragment;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public class AllLocalMusicFragment extends BaseFragment {


    @Override
    protected void initEvent(Bundle savedInstanceState) {

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_all_local_music;
    }

    public static AllLocalMusicFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AllLocalMusicFragment fragment = new AllLocalMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }
}

package com.lxw.musicplayer.ui.local;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.BaseFragment;
import com.lxw.musicplayer.ui.local.all.AllLocalMusicFragment;
import com.lxw.musicplayer.ui.local.folder.FolderFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public class LocalFilesFragment extends BaseFragment {

    @BindView(R.id.radio_button_all)
    RadioButton mRadioButtonAll;
    @BindView(R.id.radio_button_folder)
    RadioButton mRadioButtonFolder;
    @BindView(R.id.layout_fragment_container)
    FrameLayout mContainer;

    private static final int DEFAULT_SEGMENT_INDEX = 0;

    private List<Fragment> mFragmentList = new ArrayList<>(2);

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        initFragments();
        mRadioButtonAll.setChecked(true);
    }

    private void initFragments() {
        AllLocalMusicFragment allLocalMusicFragment = AllLocalMusicFragment.newInstance();
        FolderFragment folderFragemnt = FolderFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentList.add(allLocalMusicFragment);
        mFragmentList.add(folderFragemnt);
        fragmentTransaction.add(R.id.layout_fragment_container, mFragmentList.get(0), mFragmentList.get(0).getTag());
        fragmentTransaction.add(R.id.layout_fragment_container, mFragmentList.get(1), mFragmentList.get(1).getTag());
        fragmentTransaction.hide(mFragmentList.get(1));
        fragmentTransaction.commit();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_local_files;
    }

    @OnCheckedChanged({R.id.radio_button_all, R.id.radio_button_folder})
    public void onSegmentChecked(RadioButton radioButton, boolean isChecked) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (radioButton.getId()) {
            case R.id.radio_button_all:
                if (isChecked) {
                    fragmentTransaction.show(mFragmentList.get(0));
                    fragmentTransaction.hide(mFragmentList.get(1));
                } else {
                    fragmentTransaction.hide(mFragmentList.get(0));
                }
                fragmentTransaction.commit();
                break;
            case R.id.radio_button_folder:
                if (isChecked) {
                    fragmentTransaction.show(mFragmentList.get(1));
                    fragmentTransaction.hide(mFragmentList.get(0));
                } else {
                    fragmentTransaction.hide(mFragmentList.get(1));
                }
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }


}

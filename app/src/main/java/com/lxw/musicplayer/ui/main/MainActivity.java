package com.lxw.musicplayer.ui.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.widget.RadioButton;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.BaseActivity;
import com.lxw.musicplayer.base.BaseFragment;
import com.lxw.musicplayer.ui.local.LocalFilesFragment;
import com.lxw.musicplayer.ui.music.MusicPlayerFragment;
import com.lxw.musicplayer.ui.playlist.PlaylistFragment;
import com.lxw.musicplayer.ui.settings.SettingsFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MainActivity extends BaseActivity {
    static final int DEFAULT_PAGE_INDEX = 2;

    @BindViews({R.id.rdo_play_list, R.id.rdo_music, R.id.rdo_local_files, R.id.rdo_settings})
    List<RadioButton> radioButtons;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private String[] mTitles;
    private MainPagerAdapter mMainPagerAdapter;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        //main control titles
        mTitles = getResources().getStringArray(R.array.mp_main_titles);
        //basefragments
        BaseFragment[] baseFragments = new BaseFragment[mTitles.length];
        baseFragments[0] = new PlaylistFragment();
        baseFragments[1] = new MusicPlayerFragment();
        baseFragments[2] = new LocalFilesFragment();
        baseFragments[3] = new SettingsFragment();
        mMainPagerAdapter=new MainPagerAdapter(getSupportFragmentManager(),mTitles,baseFragments);
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.setOffscreenPageLimit(mMainPagerAdapter.getCount() - 1);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.mp_margin_large));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //empty
            }

            @Override
            public void onPageSelected(int position) {
                radioButtons.get(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //empty
            }
        });

        radioButtons.get(DEFAULT_PAGE_INDEX).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        //进入后台，Activty没有销毁
        moveTaskToBack(true);
    }

    @OnCheckedChanged({R.id.rdo_play_list, R.id.rdo_music, R.id.rdo_local_files, R.id.rdo_settings})
    public void onRadioButtonChecked(RadioButton button, boolean isChecked) {
        if (isChecked) {
            onItemChecked(radioButtons.indexOf(button));
        }
    }

    private void onItemChecked(int position) {
        mViewPager.setCurrentItem(position);
        mToolbar.setTitle(mTitles[position]);
    }
}

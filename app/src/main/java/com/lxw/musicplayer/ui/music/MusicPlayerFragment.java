package com.lxw.musicplayer.ui.music;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.RxBus;
import com.lxw.musicplayer.base.BaseFragment;
import com.lxw.musicplayer.data.model.PlayList;
import com.lxw.musicplayer.data.model.Song;
import com.lxw.musicplayer.data.source.PreferenceManager;
import com.lxw.musicplayer.event.PlayListNowEvent;
import com.lxw.musicplayer.event.PlaySongEvent;
import com.lxw.musicplayer.player.IPlayback;
import com.lxw.musicplayer.player.PlayMode;
import com.lxw.musicplayer.player.PlayService;
import com.lxw.musicplayer.utils.AlbumUtils;
import com.lxw.musicplayer.utils.TimeUtils;
import com.lxw.musicplayer.widget.ShadowImageView;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public class MusicPlayerFragment extends BaseFragment
        implements MusicPlayerContract.MusicView,
        IPlayback.Callback {

    // Update seek bar every second
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;

    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_artist)
    TextView mTvArtist;
    @BindView(R.id.text_view_progress)
    TextView mTextViewProgress;
    @BindView(R.id.seek_bar)
    AppCompatSeekBar mSeekBar;
    @BindView(R.id.text_view_duration)
    TextView mTextViewDuration;
    @BindView(R.id.iv_album)
    ShadowImageView mIvAlbum;
    @BindView(R.id.button_play_mode_toggle)
    AppCompatImageView mButtonPlayModeToggle;
    @BindView(R.id.button_favorite_toggle)
    AppCompatImageView mButtonFavoriteToggle;
    @BindView(R.id.button_play_toggle)
    AppCompatImageView mButtonPlayToggle;
    private MusicPlayerContract.MusicPresenter mMusicPresenter;
    private Handler mHandler = new Handler();
    //每秒对seekbar的进度条进行修改
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            if (mPlayer.isPlaying()) {
                int progress = (int) (mSeekBar.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(progress);
                if (progress >= 0 && progress <= mSeekBar.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mSeekBar.setProgress(progress, true);
                    } else {
                        mSeekBar.setProgress(progress);
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }

            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onDestroyView() {
        unbindPlayService();
        super.onDestroyView();
    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        mMusicPresenter = new MusicPresenter(this);
        bindPlayService();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(getDuration(seekBar.getProgress()));
                if (mPlayer.isPlaying()) {
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.post(mProgressCallback);
                }
            }
        });
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_music_player;
    }


    @OnClick({R.id.button_play_mode_toggle, R.id.button_play_last, R.id.button_play_toggle,
            R.id.button_play_next, R.id.button_favorite_toggle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_play_mode_toggle:
                if (mPlayer == null) return;
                PlayMode current = PreferenceManager.lastPlayMode(getActivity());
                PlayMode newMode = PlayMode.switchNextMode(current);
                PreferenceManager.setPlayMode(getActivity(), newMode);
                mPlayer.setPlayMode(newMode);
                updatePlayMode(newMode);
                break;
            case R.id.button_play_last:
                if (mPlayer == null) return;
                mPlayer.playLast();
                break;
            case R.id.button_play_toggle:
                if (mPlayer == null) return;

                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.play();
                }

                break;
            case R.id.button_play_next:
                if (mPlayer == null) return;
                mPlayer.playNext();
                break;
            case R.id.button_favorite_toggle:
                if (mPlayer == null) return;

                Song currentSong = mPlayer.getPlayingSong();
                if (currentSong != null) {
                    view.setEnabled(false);
                    mMusicPresenter.setSongAsFavorite(currentSong, !currentSong.isFavorite());
                }
                break;

        }
    }

    @Override
    public void handleError(Throwable throwable) {
        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSongSetAsFavorite(@NonNull Song song) {
        mButtonFavoriteToggle.setEnabled(true);
        updateFavoriteToggle(song.isFavorite());
    }

    @Override
    public void setPresenter(MusicPlayerContract.MusicPresenter presenter) {
        mMusicPresenter = presenter;
    }

    private PlayService mPlayer;

    private boolean mIsServiceBind;

    private void bindPlayService() {
        Intent service = new Intent(mContext, PlayService.class);
        mContext.bindService(service, mConnection, Service.BIND_AUTO_CREATE);
        mIsServiceBind = true;
    }

    private void unbindPlayService() {
        if (mIsServiceBind) {
            mContext.unbindService(mConnection);
            mIsServiceBind = false;
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.LocalBinder localBinder = (PlayService.LocalBinder) service;
            mPlayer = localBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer = null;
        }
    };

    @Override
    public void onSwitchLast(@Nullable Song last) {
        onSongUpdated(last);
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        onSongUpdated(next);
    }

    @Override
    public void onComplete(@Nullable Song next) {
        onSongUpdated(next);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        updatePlayToggle(isPlaying);
        if (isPlaying) {
            mIvAlbum.resumeRotateAnimation();
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mIvAlbum.pauseRotateAnimation();
            mHandler.removeCallbacks(mProgressCallback);
        }
    }

    private int getCurrentSongDuration() {
        Song currentSong = mPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    private void updateProgressTextWithDuration(int duration) {
        mTextViewDuration.setText(TimeUtils.formatDuration(duration));
    }

    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        mTextViewProgress.setText(TimeUtils.formatDuration(targetDuration));
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / mSeekBar.getMax()));
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private void updateFavoriteToggle(boolean favorite) {
        mButtonFavoriteToggle.setImageResource(favorite ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
    }

    private void updatePlayToggle(boolean play) {
        mButtonPlayToggle.setImageResource(play ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updatePlayMode(PlayMode playMode) {
        if (playMode == null) {
            playMode = PlayMode.getDefault();
        }
        switch (playMode) {
            case LIST:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_list);
                break;
            case LOOP:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_loop);
                break;
            case SHUFFLE:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_shuffle);
                break;
            case SINGLE:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_single);
                break;
        }
    }

    public void onSongUpdated(@Nullable Song song) {
        if (song == null) {
            mIvAlbum.cancelRotateAnimation();
            mButtonPlayToggle.setImageResource(R.drawable.ic_play);
            mSeekBar.setProgress(0);
            updateProgressTextWithProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }

        // Step 1: Song name and artist
        mTvName.setText(song.getDisplayName());
        mTvArtist.setText(song.getArtist());
        // Step 2: favorite
        mButtonFavoriteToggle.setImageResource(song.isFavorite() ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
        // Step 3: Duration
        mTextViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        Bitmap bitmap = AlbumUtils.parseAlbum(song);
        if (bitmap == null) {
            mIvAlbum.setImageResource(R.drawable.default_record_album);
        } else {
            mIvAlbum.setImageBitmap(AlbumUtils.getCroppedBitmap(bitmap));
        }
        mIvAlbum.pauseRotateAnimation();
        mHandler.removeCallbacks(mProgressCallback);
        if (mPlayer.isPlaying()) {
            mIvAlbum.startRotateAnimation();
            mHandler.post(mProgressCallback);
            mButtonPlayToggle.setImageResource(R.drawable.ic_pause);
        }
    }
    // Music Controls

    private void playSong(Song song) {
        PlayList playList = new PlayList(song);
        playSong(playList, 0);
    }

    private void playSong(PlayList playList, int playIndex) {
        if (playList == null) return;

        playList.setPlayMode(PreferenceManager.lastPlayMode(getActivity()));
        // boolean result =
        mPlayer.play(playList, playIndex);

        Song song = playList.getCurrentSong();
        onSongUpdated(song);
    }

    @Override
    protected Subscription subscribeEvents() {
        return RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof PlaySongEvent) {
                            onPlaySongEvent((PlaySongEvent) o);
                        } else if (o instanceof PlayListNowEvent) {
                            onPlayListNowEvent((PlayListNowEvent) o);
                        }
                    }
                })
                .subscribe(RxBus.defaultSubscriber());
    }

    private void onPlaySongEvent(PlaySongEvent event) {
        Song song = event.song;
        playSong(song);
    }

    private void onPlayListNowEvent(PlayListNowEvent event) {
        PlayList playList = event.playList;
        int playIndex = event.playIndex;
        playSong(playList, playIndex);
    }

}

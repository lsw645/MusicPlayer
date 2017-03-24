package com.lxw.musicplayer.player;

import android.media.MediaPlayer;
import android.util.Log;

import com.lxw.musicplayer.data.model.PlayList;
import com.lxw.musicplayer.data.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public class Player implements IPlayback ,MediaPlayer.OnCompletionListener{

    private static final String TAG = "Player";

    private static volatile Player sInstance;

    private MediaPlayer mMediaPlayer;

    private PlayList mPlayList;

    // Default size 2: for service and UI
    private List<Callback> mCallbacks = new ArrayList<>(2);

    // Player status
    private boolean isPaused;

    private Player() {
        mMediaPlayer = new MediaPlayer();
        mPlayList = new PlayList();
        mMediaPlayer.setOnCompletionListener(this);
    }

    public static Player getInstance() {
        if (sInstance == null) {
            synchronized (Player.class) {
                if (sInstance == null) {
                    sInstance = new Player();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void setPlayList(PlayList playList) {
        if (playList == null) {
            return;
        }
        mPlayList = playList;
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mMediaPlayer.start();
            notifyPlayStatusChanged(true);
            return true;
        }
        if (mPlayList.prepare()) {
            Song song = mPlayList.getCurrentSong();
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(song.getPath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                notifyPlayStatusChanged(true);
                return true;
            } catch (IOException e) {
                Log.e(TAG, "play: ", e);
                notifyPlayStatusChanged(false);
            }
        }

        return false;
    }

    @Override
    public boolean play(PlayList playList) {
        if (playList == null) return false;
        isPaused = false;
        setPlayList(playList);
        return play();
    }

    @Override
    public boolean play(PlayList list, int startIndex) {
        if (list == null || startIndex < 0 || startIndex > list.getNumOfSongs()) return false;
        isPaused = false;
        list.setPlayingIndex(startIndex);
        setPlayList(list);
        return play();
    }

    @Override
    public boolean play(Song song) {
        if (song == null) return false;
        mPlayList.getSongs().clear();
        mPlayList.getSongs().add(song);
        isPaused = false;
        return play();
    }

    @Override
    public boolean playLast() {
        isPaused = false;
        boolean hasLast = mPlayList.hasLast();
        if (hasLast) {
            Song song = mPlayList.last();
            play();
            notifyPlayLast(song);
            return true;
        }
        return false;
    }

    @Override
    public boolean playNext() {
        isPaused = false;
        boolean hasNext = mPlayList.hasNext(false);
        if (hasNext) {

            Song song = mPlayList.next();
            play();
            notifyPlayNext(song);
            return true;
        }
        return false;
    }

    @Override
    public boolean pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPaused = true;
            notifyPlayStatusChanged(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getProgress() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public Song getPlayingSong() {
        return mPlayList.getCurrentSong();
    }

    @Override
    public boolean seekTo(int progress) {
        if(mPlayList.getSongs().size()==0) return  false;
        Song currentSong=mPlayList.getCurrentSong();
        if(currentSong!=null){
            if(currentSong.getDuration()<=progress){
                onCompletion(mMediaPlayer);
            }else{
                mMediaPlayer.seekTo(progress);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setPlayMode(PlayMode playMode) {
        mPlayList.setPlayMode(playMode);
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void removeCallbacks() {
        mCallbacks.clear();
    }

    @Override
    public void releasePlayer() {
        mPlayList = null;
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        sInstance = null;
    }

    private void notifyPlayStatusChanged(boolean isPlaying) {
        for (Callback callback : mCallbacks) {
            callback.onPlayStatusChanged(isPlaying);
        }
    }

    private void notifyPlayLast(Song song) {
        for (Callback callback : mCallbacks) {
            callback.onSwitchLast(song);
        }
    }

    private void notifyPlayNext(Song song) {
        for (Callback callback : mCallbacks) {
            callback.onSwitchNext(song);
        }
    }

    private void notifyComplete(Song song) {
        for (Callback callback : mCallbacks) {
            callback.onComplete(song);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Song next = null;
        // There is only one limited play mode which is list, player should be stopped when hitting the list end
        if (mPlayList.getPlayMode() == PlayMode.LIST && mPlayList.getPlayingIndex() == mPlayList.getNumOfSongs() - 1) {
            // In the end of the list
            // Do nothing, just deliver the callback
        } else if (mPlayList.getPlayMode() == PlayMode.SINGLE) {
            next = mPlayList.getCurrentSong();
            play();
        } else {
            boolean hasNext = mPlayList.hasNext(true);
            if (hasNext) {
                next = mPlayList.next();
                play();
            }
        }
        notifyComplete(next);
    }
}

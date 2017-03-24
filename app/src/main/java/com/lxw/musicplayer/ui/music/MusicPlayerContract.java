package com.lxw.musicplayer.ui.music;

import android.support.annotation.NonNull;

import com.lxw.musicplayer.base.BasePresenter;
import com.lxw.musicplayer.base.BaseView;
import com.lxw.musicplayer.data.model.Song;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public interface MusicPlayerContract {
      interface MusicView extends BaseView<MusicPresenter>{
            void handleError(Throwable throwable);
          void onSongSetAsFavorite(@NonNull Song song);
      }

    interface  MusicPresenter extends BasePresenter{
        void retrieveLastPlayMode();
        void setSongAsFavorite(Song song, boolean favorite);
    }
}

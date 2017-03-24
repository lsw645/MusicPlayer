package com.lxw.musicplayer.data.source;

import com.lxw.musicplayer.Injection;
import com.lxw.musicplayer.data.model.Folder;
import com.lxw.musicplayer.data.model.PlayList;
import com.lxw.musicplayer.data.model.Song;
import com.lxw.musicplayer.data.source.db.LiteOrmHelper;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public class AppRepository  implements AppContract{

    private static volatile AppRepository sInstance;

    private AppLocalDataSource mLocalDataSource;

    private List<PlayList> mCachedPlayLists;

    private AppRepository() {
        mLocalDataSource = new AppLocalDataSource(Injection.provideContext(), LiteOrmHelper.getInstance());
    }
    public static AppRepository getInstance() {
        if (sInstance == null) {
            synchronized (AppRepository.class) {
                if (sInstance == null) {
                    sInstance = new AppRepository();
                }
            }
        }
        return sInstance;
    }


    @Override
    public Observable<List<PlayList>> playLists() {
        return mLocalDataSource.playLists().doOnNext(new Action1<List<PlayList>>() {
            @Override
            public void call(List<PlayList> playLists) {
                mCachedPlayLists=playLists;
            }
        });
    }

    @Override
    public List<PlayList> cachedPlayLists() {
        if (mCachedPlayLists == null) {
            return new ArrayList<>(0);
        }
        return mCachedPlayLists;
    }

    @Override
    public Observable<PlayList> create(PlayList playList) {
        return mLocalDataSource.create(playList);
    }

    @Override
    public Observable<PlayList> update(PlayList playList) {
        return mLocalDataSource.update(playList);
    }

    @Override
    public Observable<PlayList> delete(PlayList playList) {
        return mLocalDataSource.delete(playList);
    }

    // Folders

    @Override
    public Observable<List<Folder>> folders() {
        return mLocalDataSource.folders();
    }

    @Override
    public Observable<Folder> create(Folder folder) {
        return mLocalDataSource.create(folder);
    }

    @Override
    public Observable<List<Folder>> create(List<Folder> folders) {
        return mLocalDataSource.create(folders);
    }

    @Override
    public Observable<Folder> update(Folder folder) {
        return mLocalDataSource.update(folder);
    }

    @Override
    public Observable<Folder> delete(Folder folder) {
        return mLocalDataSource.delete(folder);
    }

    @Override
    public Observable<List<Song>> insert(List<Song> songs) {
        return mLocalDataSource.insert(songs);
    }

    @Override
    public Observable<Song> update(Song song) {
        return mLocalDataSource.update(song);
    }

    @Override
    public Observable<Song> setSongAsFavorite(Song song, boolean favorite) {
        return mLocalDataSource.setSongAsFavorite(song, favorite);
    }
}

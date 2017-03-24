package com.lxw.musicplayer.ui.music;

import com.lxw.musicplayer.Injection;
import com.lxw.musicplayer.RxBus;
import com.lxw.musicplayer.data.model.Song;
import com.lxw.musicplayer.data.source.AppRepository;
import com.lxw.musicplayer.data.source.PreferenceManager;
import com.lxw.musicplayer.event.FavoriteChangeEvent;
import com.lxw.musicplayer.player.PlayMode;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/22
 * @see //TODO
 * @since JDK 1.8
 */

public class MusicPresenter implements MusicPlayerContract.MusicPresenter {

    private AppRepository mAppRepository;

    private MusicPlayerContract.MusicView mView;

    private CompositeSubscription mSubscription;

    public MusicPresenter(MusicPlayerContract.MusicView view) {
        mView = view;
        mAppRepository=AppRepository.getInstance();
        mSubscription=new CompositeSubscription();
        mView.setPresenter(this);
    }

    @Override
    public void retrieveLastPlayMode() {
        PlayMode lastPlayMode = PreferenceManager.lastPlayMode(Injection.provideContext());
    }

    @Override
    public void setSongAsFavorite(Song song, boolean favorite) {
        Subscription subscription = mAppRepository.setSongAsFavorite(song, favorite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Song>() {
                    @Override
                    public void onCompleted() {
                        // Empty
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(Song song) {
                        mView.onSongSetAsFavorite(song);
                        RxBus.getInstance().post(new FavoriteChangeEvent(song));
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}

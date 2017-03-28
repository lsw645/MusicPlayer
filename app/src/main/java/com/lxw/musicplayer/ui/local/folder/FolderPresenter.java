package com.lxw.musicplayer.ui.local.folder;

import com.lxw.musicplayer.RxBus;
import com.lxw.musicplayer.data.model.Folder;
import com.lxw.musicplayer.data.model.PlayList;
import com.lxw.musicplayer.data.model.Song;
import com.lxw.musicplayer.data.source.AppRepository;
import com.lxw.musicplayer.event.PlayListUpdatedEvent;
import com.lxw.musicplayer.utils.FileUtils;
import com.lxw.musicplayer.utils.RxUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public class FolderPresenter implements FolderContract.Presenter {

    private AppRepository mAppRepository;
    private FolderContract.View mView;
    private CompositeSubscription mSubscriptions;

    public FolderPresenter(FolderContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mSubscriptions = new CompositeSubscription();
        mAppRepository = AppRepository.getInstance();
    }

    @Override
    public void subscribe() {
        loadFolders();
    }

    @Override
    public void unsubscribe() {
        mView = null;
        mSubscriptions.clear();
    }

    @Override
    public void loadFolders() {
        Subscription subscription = mAppRepository.folders()
                .compose(RxUtils.<List<Folder>>rxSchedulerHelper())
                .doOnNext(new Action1<List<Folder>>() {
                    @Override
                    public void call(List<Folder> folders) {
                        Collections.sort(folders, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder o1, Folder o2) {
                                return o1.getName().compareToIgnoreCase(o2.getName());
                            }
                        });
                    }
                })
                .subscribe(new Subscriber<List<Folder>>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                        Logger.d("onStart");
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                        Logger.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                        Logger.d(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Folder> folders) {
                        Logger.d("onNext");
                        mView.onFoldersLoaded(folders);

                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void refreshFolder(final Folder folder) {
        Subscription subscription = Observable
                .just(FileUtils.musicFiles(new File(folder.getPath())))
                .flatMap(new Func1<List<Song>, Observable<Folder>>() {
                    @Override
                    public rx.Observable<Folder> call(List<Song> songs) {
                        folder.setSongs(songs);
                        folder.setNumOfSongs(songs.size());
                        return mAppRepository.update(folder);
                    }
                })
                .compose(RxUtils.<Folder>rxSchedulerHelper())
                .subscribe(new Subscriber<Folder>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(Folder folder) {
                        mView.onFolderUpdated(folder);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deleteFolder(Folder folder) {
        Subscription subscription = mAppRepository.delete(folder)
                .compose(RxUtils.<Folder>rxSchedulerHelper())
                .subscribe(new Subscriber<Folder>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(Folder folder) {
                        mView.onFolderDeleted(folder);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void createPlayList(PlayList playList) {
        Subscription subscription = mAppRepository
                .create(playList)
                .compose(RxUtils.<PlayList>rxSchedulerHelper())
                .subscribe(new Subscriber<PlayList>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(PlayList playList) {
                        mView.onPlayListCreated(playList);
                    }
                });
        mSubscriptions.add(subscription);

    }

    @Override
    public void addFolderToPlayList(Folder folder, PlayList playList) {
        if (folder.getSongs().isEmpty()) return;

        if (playList.isFavorite()) {
            for (Song song : folder.getSongs()) {
                song.setFavorite(true);
            }
        }
        playList.addSong(folder.getSongs(), 0);
        Subscription subscription = mAppRepository.update(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlayList>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(PlayList playList) {
                        RxBus.getInstance().post(new PlayListUpdatedEvent(playList));
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void addFolders(List<File> folders, final List<Folder> existedFolders) {
        Subscription subscription = Observable.from(folders)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        for (Folder folder : existedFolders) {
                            if (file.getAbsolutePath().equals(file.getPath())) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .flatMap(new Func1<File, Observable<Folder>>() {
                    @Override
                    public Observable<Folder> call(File file) {
                        Folder folder = new Folder();
                        folder.setName(file.getName());
                        folder.setPath(file.getAbsolutePath());
                        List<Song> musicFile = FileUtils.musicFiles(file);
                        folder.setSongs(musicFile);
                        folder.setNumOfSongs(musicFile.size());
                        return Observable.just(folder);
                    }
                })
                .toList()
                .flatMap(new Func1<List<Folder>, Observable<List<Folder>>>() {
                    @Override
                    public Observable<List<Folder>> call(List<Folder> folders) {
                        return mAppRepository.create(folders);
                    }
                })
                .doOnNext(new Action1<List<Folder>>() {
                    @Override
                    public void call(final List<Folder> folders) {
                        Collections.sort(folders, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder o1, Folder o2) {
                                return o1.getName().compareToIgnoreCase(o2.getName());
                            }
                        });
                    }
                })
                .compose(RxUtils.<List<Folder>>rxSchedulerHelper())
                .subscribe(new Subscriber<List<Folder>>() {

                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(List<Folder> folders) {
                        mView.onFoldersAdded(folders);
                    }
                });
        mSubscriptions.add(subscription);

    }
}

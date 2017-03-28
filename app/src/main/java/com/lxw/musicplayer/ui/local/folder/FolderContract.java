package com.lxw.musicplayer.ui.local.folder;

import com.lxw.musicplayer.base.BasePresenter;
import com.lxw.musicplayer.base.BaseView;
import com.lxw.musicplayer.data.model.Folder;
import com.lxw.musicplayer.data.model.PlayList;

import java.util.List;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public interface FolderContract {


    interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void handleError(Throwable error);

        void onFoldersLoaded(List<Folder> folders);

        void onFolderUpdated(Folder folder);

        void onFolderDeleted(Folder folder);

        void onPlayListCreated(PlayList playList);
    }

    interface Presenter extends BasePresenter {
        void loadFolders();

        void refreshFolder(Folder folder);

        void deleteFolder(Folder folder);

        void createPlayList(PlayList playList);

        void addFolderToPlayList(Folder folder, PlayList playList);
    }

}

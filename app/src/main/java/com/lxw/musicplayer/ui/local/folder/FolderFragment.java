package com.lxw.musicplayer.ui.local.folder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.RxBus;
import com.lxw.musicplayer.base.BaseFragment;
import com.lxw.musicplayer.base.adapter.OnItemClickListener;
import com.lxw.musicplayer.data.model.Folder;
import com.lxw.musicplayer.data.model.PlayList;
import com.lxw.musicplayer.event.PlayListCreatedEvent;
import com.lxw.musicplayer.ui.common.DefaultDividerDecoration;
import com.lxw.musicplayer.ui.details.PlayListDetailsActivity;
import com.lxw.musicplayer.ui.local.filesystem.FileSystemActivity;
import com.lxw.musicplayer.ui.playlist.AddToPlayListDialogFragment;
import com.lxw.musicplayer.ui.playlist.EditPlayListDialogFragment;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public class FolderFragment extends BaseFragment
        implements FolderAdapter.AddFolderCallback,
        FolderContract.View {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private FolderAdapter mFolderAdapter;
    private FolderContract.Presenter mFolderPresenter;

    private int mUpdateIndex, mDeleteIndex;

    public static FolderFragment newInstance() {

        Bundle args = new Bundle();
        FolderFragment fragment = new FolderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFolderPresenter.unsubscribe();
    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        mFolderAdapter = new FolderAdapter(mContext, null);
        mFolderAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Folder folder = mFolderAdapter.getItem(position);
                startActivity(PlayListDetailsActivity.launchIntentForFolder(mContext, folder));
            }
        });
        mFolderAdapter.setAddFolderCallback(this);
        recyclerView.setAdapter(mFolderAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
        List<Folder> folders=mFolderAdapter.getData();
        new FolderPresenter(this).subscribe();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_folder;
    }

    @Override
    public void onAction(View actionView, final int position) {
        final Folder folder = mFolderAdapter.getItem(position);
        PopupMenu popupMenu = new PopupMenu(mContext, actionView, Gravity.BOTTOM | Gravity.END);
        popupMenu.inflate(R.menu.folders_action);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_add_to_play_list:
                       new  AddToPlayListDialogFragment().setCallback(new AddToPlayListDialogFragment.Callback() {
                           @Override
                           public void onPlayListSelected(PlayList playList) {
                               mFolderPresenter.addFolderToPlayList(folder, playList);
                           }
                       }).show(getFragmentManager().beginTransaction(),"AddToPlayList");

                        break;
                    case R.id.menu_item_create_play_list:
                        PlayList playList = PlayList.fromFolder(folder);
                        EditPlayListDialogFragment.editPlayList(playList)
                                .setCallback(new EditPlayListDialogFragment.Callback() {
                                    @Override
                                    public void onCreated(PlayList playList) {

                                    }

                                    @Override
                                    public void onEdited(PlayList playList) {
                                        mFolderPresenter.createPlayList(playList);
                                    }
                                })
                                .show(getFragmentManager().beginTransaction(), "CreatePlayList");

                        break;
                    case R.id.menu_item_refresh:
                        mUpdateIndex = position;
                        mFolderPresenter.refreshFolder(folder);
                        break;
                    case R.id.menu_item_delete:
                        mDeleteIndex = position;
                        mFolderPresenter.deleteFolder(folder);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }


    @Override
    public void addFolder() {
        startActivity(new Intent(getActivity(), FileSystemActivity.class));
    }

    @Override
    public void setPresenter(FolderContract.Presenter presenter) {
        mFolderPresenter = presenter;
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFoldersLoaded(List<Folder> folders) {
        Logger.d(folders.size()+"sdadadadada");
        mFolderAdapter.setData(folders);
        mFolderAdapter.notifyDataSetChanged();
        List<Folder> folderList = mFolderAdapter.getData();
        Logger.d(folderList.size());
    }

    @Override
    public void onFolderUpdated(Folder folder) {
        mFolderAdapter.getData().set(mUpdateIndex, folder);
        mFolderAdapter.notifyItemChanged(mUpdateIndex);
    }

    @Override
    public void onFolderDeleted(Folder folder) {
        mFolderAdapter.getData().remove(mDeleteIndex);
        mFolderAdapter.notifyItemRemoved(mDeleteIndex);
    }

    @Override
    public void onPlayListCreated(PlayList playList) {
        RxBus.getInstance().post(new PlayListCreatedEvent(playList));
        Toast.makeText(getActivity(), getString(R.string.mp_play_list_created, playList.getName()), Toast.LENGTH_SHORT).show();
    }


}

package com.lxw.musicplayer.ui.playlist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.BaseDialogFragment;
import com.lxw.musicplayer.base.adapter.ListAdapter;
import com.lxw.musicplayer.base.adapter.OnItemClickListener;
import com.lxw.musicplayer.data.model.PlayList;
import com.lxw.musicplayer.data.source.AppRepository;
import com.lxw.musicplayer.ui.common.DefaultDividerDecoration;

import java.util.List;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/28
 * @see //TODO
 * @since JDK 1.8
 */

public class AddToPlayListDialogFragment extends BaseDialogFragment
        implements OnItemClickListener,
        DialogInterface.OnShowListener{
    RecyclerView mRecyclerView;

    AddPlayListAdapter mAdapter;
    Callback mCallback;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AddPlayListAdapter(getContext(), AppRepository.getInstance().cachedPlayLists());
        mAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog =new AlertDialog.Builder(getContext())
                .setTitle(R.string.mp_play_list_dialog_add_to)
                .setView(R.layout.dialog_add_to_play_list)
                .setNegativeButton(R.string.mp_cancel,null)
                .create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onClick(int position) {
        if (mCallback != null) {
            mCallback.onPlayListSelected(mAdapter.getItem(position));
        }
        dismiss();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        resizeDialogSize();
        if (mRecyclerView == null) {
            mRecyclerView = (RecyclerView) getDialog().findViewById(R.id.recycler_view);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new DefaultDividerDecoration());
        }
    }
    public AddToPlayListDialogFragment setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    private static class AddPlayListAdapter extends ListAdapter<PlayList, PlayListItemView> {
        public AddPlayListAdapter(Context context, List<PlayList> data) {
            super(context, data);
        }

        @Override
        protected PlayListItemView createView(Context context) {
            return new PlayListItemView(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
            if (holder.itemView instanceof PlayListItemView) {
                PlayListItemView itemView = (PlayListItemView) holder.itemView;
                itemView.buttonAction.setVisibility(View.GONE);
            }
            return holder;
        }
    }

    public interface Callback {
        void onPlayListSelected(PlayList playList);
    }
}

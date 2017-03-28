package com.lxw.musicplayer.ui.playlist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.BaseDialogFragment;
import com.lxw.musicplayer.data.model.PlayList;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public class EditPlayListDialogFragment extends BaseDialogFragment implements Dialog.OnShowListener {

    private static final String ARGUMENT_PLAY_LIST = "playList";
    private EditText editTextName;
    private PlayList mPlayList;

    private Callback mCallback;

    public static EditPlayListDialogFragment newInstance(@Nullable PlayList playList) {
        EditPlayListDialogFragment fragment = new EditPlayListDialogFragment();
        if (playList != null) {
            Bundle args = new Bundle();
            args.putParcelable(ARGUMENT_PLAY_LIST, playList);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public static EditPlayListDialogFragment newInstance() {
        return newInstance(null);
    }

    public static EditPlayListDialogFragment createPlayList() {
        return newInstance();
    }

    public static EditPlayListDialogFragment editPlayList(PlayList playList) {
        return newInstance(playList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPlayList = arguments.getParcelable(ARGUMENT_PLAY_LIST);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getTitle())
                .setView(R.layout.dialog_create_or_edit_play_list)
                .setNegativeButton(R.string.mp_cancel, null)
                .setPositiveButton(R.string.mp_Confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConfirm();
                    }
                })
                .create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        resizeDialogSize();
        if (editTextName == null) {
            editTextName = (EditText) getDialog().findViewById(R.id.edit_text);
            editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                   if(actionId== EditorInfo.IME_ACTION_DONE){
                       if(editTextName.length()>0){
                           onConfirm();
                       }
                       return  true;
                   }
                    return false;
                }
            });
        }
        if (isEditMode()) {
            editTextName.setText(mPlayList.getName());
        }
        editTextName.requestFocus();
        editTextName.setSelection(editTextName.length());
    }

    private void onConfirm() {
        if (mCallback == null) return;
        PlayList playList = mPlayList;
        if (playList == null) {
            playList = new PlayList();
        }
        if (isEditMode()) {
            mCallback.onEdited(playList);
        } else {
            mCallback.onCreated(playList);
        }
    }


    public EditPlayListDialogFragment setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }


    private String getTitle() {
        return getString(isEditMode() ?
                R.string.mp_play_list_edit : R.string.mp_play_list_create);
    }

    private boolean isEditMode() {
        return mPlayList != null;
    }


    public interface Callback {

        void onCreated(PlayList playList);

        void onEdited(PlayList playList);
    }
}

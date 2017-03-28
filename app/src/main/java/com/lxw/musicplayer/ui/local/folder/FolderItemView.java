package com.lxw.musicplayer.ui.local.folder;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.adapter.IAdapterView;
import com.lxw.musicplayer.data.model.Folder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public class FolderItemView extends RelativeLayout implements IAdapterView<Folder> {

    @BindView(R.id.text_view_name)
    TextView mTextViewName;
    @BindView(R.id.text_view_info)
    TextView mTextViewInfo;
    @BindView(R.id.layout_action)
    View mBtnAction;

    public FolderItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_added_folder, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(Folder folder, int position) {
        mTextViewName.setText(folder.getName());
        mTextViewInfo.setText(getContext().getString(
                R.string.mp_local_files_folder_list_item_info_formatter,
                folder.getNumOfSongs(),
                folder.getPath()
        ));
    }
}

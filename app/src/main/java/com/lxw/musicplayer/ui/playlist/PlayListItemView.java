package com.lxw.musicplayer.ui.playlist;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.adapter.IAdapterView;
import com.lxw.musicplayer.data.model.PlayList;
import com.lxw.musicplayer.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/28
 * @see //TODO
 * @since JDK 1.8
 */

public class PlayListItemView extends RelativeLayout
        implements IAdapterView<PlayList> {

    @BindView(R.id.image_view_album)
    AppCompatImageView imageViewAlbum;
    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_info)
    TextView textViewInfo;
    @BindView(R.id.layout_action)
    View buttonAction;

    public PlayListItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_play_list, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(PlayList item, int position) {
        if (item.isFavorite()) {
            imageViewAlbum.setImageResource(R.drawable.ic_favorite_yes);
        } else {
            imageViewAlbum.setImageDrawable(ViewUtils.generateAlbumDrawable(getContext(), item.getName()));
        }
    }
}

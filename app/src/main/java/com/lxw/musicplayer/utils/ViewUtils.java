package com.lxw.musicplayer.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.widget.CharacterDrawable;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/28
 * @see //TODO
 * @since JDK 1.8
 */

public class ViewUtils {

    public static CharacterDrawable generateAlbumDrawable(Context context, String albumName) {
        if (context == null || albumName == null) return null;

        return new CharacterDrawable.Builder()
                .setCharacter(albumName.length() == 0 ? ' ' : albumName.charAt(0))
                .setBackgroundColor(ContextCompat.getColor(context, R.color.mp_characterView_background))
                .setCharacterTextColor(ContextCompat.getColor(context, R.color.mp_characterView_textColor))
                .setCharacterPadding(context.getResources().getDimensionPixelSize(R.dimen.mp_characterView_padding))
                .build();
    }
}

package com.lxw.musicplayer;

import android.content.Context;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public class Injection {

    public static Context provideContext() {
        return MusicApp.getInstance();
    }

}

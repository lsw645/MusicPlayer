package com.lxw.musicplayer;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public class MusicApp extends Application {
    private static MusicApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        // Custom fonts
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Monospace-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public static MusicApp getInstance() {
        return sInstance;
    }

}

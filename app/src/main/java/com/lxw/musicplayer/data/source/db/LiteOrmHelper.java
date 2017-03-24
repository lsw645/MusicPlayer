package com.lxw.musicplayer.data.source.db;

import com.litesuits.orm.LiteOrm;
import com.lxw.musicplayer.BuildConfig;
import com.lxw.musicplayer.Injection;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/21
 * @see //TODO
 * @since JDK 1.8
 */

public class LiteOrmHelper {
    private static final String DB_NAME="music-player.db";

    private static volatile LiteOrm sInstance;

    private LiteOrmHelper(){

    }
    public static  LiteOrm getInstance(){
        if(sInstance==null){
            synchronized (LiteOrmHelper.class){
                if(sInstance==null){
                    sInstance=LiteOrm.newCascadeInstance(Injection.provideContext(),DB_NAME);
                    sInstance.setDebugged(BuildConfig.DEBUG);
                }
            }
        }
        return sInstance;
    }
}

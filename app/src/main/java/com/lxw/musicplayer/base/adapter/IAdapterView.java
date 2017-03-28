package com.lxw.musicplayer.base.adapter;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public interface IAdapterView<T> {
    void bind(T item,int position);
}

package com.lxw.musicplayer.ui.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.base.adapter.IAdapterView;
import com.lxw.musicplayer.base.adapter.ListAdapter;

import java.util.List;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public abstract class AbstractSummaryAdapter<K, V extends IAdapterView> extends ListAdapter<K, V> {
    protected static final int VIEW_TYPE_ITEM = 1; //Normal list item
    protected static final int VIEW_TYPE_EMD = 2;  //End summary item, e.g. '2 items in total'

    private Context mContext;

    private boolean hasEndSummary;
    private TextView mTvSummary;

    public AbstractSummaryAdapter(Context context, List<K> data) {
        super(context, data);
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateSummaryText();
            }
        });
    }

    private void updateSummaryText() {
        if (mTvSummary != null) {
            mTvSummary.setText(getEndSummaryText(getItemCount()));
        }
    }

    protected abstract String getEndSummaryText(int dataCount);


    @Override
    public int getItemCount() {
        int itemCount=super.getItemCount();
        if(hasEndSummary){
            itemCount =itemCount+1;
        }
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if(hasEndSummary&&position==getItemCount()-1){
            return VIEW_TYPE_EMD;
        }
        return VIEW_TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_EMD){
            mTvSummary = (TextView) LayoutInflater.from(mContext).inflate(R.layout.default_list_end_summary,parent,false);
            return  new RecyclerView.ViewHolder(mTvSummary) {};
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_ITEM){
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public K getItem(int position) {
        if(getItemViewType(position)==VIEW_TYPE_EMD){
            return  null;
        }
        return super.getItem(position);
    }
}

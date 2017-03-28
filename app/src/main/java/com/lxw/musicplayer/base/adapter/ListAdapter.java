package com.lxw.musicplayer.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public abstract class ListAdapter<T, V extends IAdapterView> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context mContext;
    private List<T> mData;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private int mLastItemClickPosition = RecyclerView.NO_POSITION;


    public ListAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
    }

    protected abstract V createView(Context context);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = (View) createView(mContext);
        final RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(itemView) {
        };
        if (mOnItemClickListener != null) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onClick(position);
                    }
                }
            });
        }

        if (mOnItemLongClickListener != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mOnItemLongClickListener.onItemClick(position);
                    }
                    return false;
                }
            });
        }
        return viewHolder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IAdapterView itemView = (IAdapterView) holder.itemView;
        itemView.bind(getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
    }

    public void addData(List<T> data) {
        if (mData == null) {
            mData = data;
        } else {
            mData.addAll(data);
        }
    }

   public T getItem(int position){
       return mData.get(position);
   }
   public void clear(){
       if(mData!=null){
           mData.clear();
       }
   }

    public OnItemClickListener getItemClickListener() {
        return mOnItemClickListener;
    }

    public int getLastItemClickPosition() {
        return mLastItemClickPosition;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public OnItemLongClickListener getItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }
}

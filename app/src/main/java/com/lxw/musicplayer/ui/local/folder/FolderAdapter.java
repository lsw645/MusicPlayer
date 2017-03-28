package com.lxw.musicplayer.ui.local.folder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxw.musicplayer.R;
import com.lxw.musicplayer.data.model.Folder;
import com.lxw.musicplayer.ui.common.AbstractFooterAdapter;

import java.util.List;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public class FolderAdapter extends AbstractFooterAdapter<Folder,FolderItemView> {
    private AddFolderCallback mAddFolderCallback;
    private View mFooterView;
    private TextView textViewSummary;
    public FolderAdapter(Context context, List<Folder> data) {
        super(context, data);
    }

    @Override
    protected FolderItemView createView(Context context) {
        return new FolderItemView(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder=super.onCreateViewHolder(parent,viewType);
        if(holder.itemView instanceof FolderItemView){
            final FolderItemView itemView= (FolderItemView) holder.itemView;
            itemView.mBtnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =holder.getAdapterPosition();
                    if(mAddFolderCallback!=null){
                        mAddFolderCallback.onAction(itemView.mBtnAction,position);
                    }
                }
            });
        }
        return holder;
    }


    @Override
    protected boolean isFooterEnabled() {
        return true;
    }

    @Override
    protected View createFooterView() {
        if(mFooterView==null){
            mFooterView=View.inflate(mContext, R.layout.item_local_folder_footer,null);
            View addView =mFooterView.findViewById(R.id.layout_add_folder);
            textViewSummary = (TextView) mFooterView.findViewById(R.id.text_view_summary);
            addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(mAddFolderCallback!=null){
                    mAddFolderCallback.addFolder();
                }
                }
            });
        }
        updateFooterView();
        return mFooterView;
    }

    public void updateFooterView() {
        if (textViewSummary == null) return;

        int itemCount = getItemCount() - 1; // real data count
        if (itemCount > 1) {
            textViewSummary.setVisibility(View.VISIBLE);
            textViewSummary.setText(mContext.getString(R.string.mp_local_files_folder_list_end_summary_formatter, itemCount));
        } else {
            textViewSummary.setVisibility(View.GONE);
        }
    }

    public void setAddFolderCallback(AddFolderCallback addFolderCallback) {
        mAddFolderCallback = addFolderCallback;
    }

    public interface AddFolderCallback{
        void onAction(View actionView,int position);
        void addFolder();
    }
}

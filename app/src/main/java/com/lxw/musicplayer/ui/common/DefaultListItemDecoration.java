package com.lxw.musicplayer.ui.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * description... //TODO
 *
 * @author lsw
 * @version 1.0, 2017/3/27
 * @see //TODO
 * @since JDK 1.8
 */

public class DefaultListItemDecoration extends RecyclerView.ItemDecoration {
    private static final int DIVIDER_HEIGHT = 1; // 1 pixel
    private static final int BACKGROUND_COLOR = 0x1FDDF9FF;
    private  Paint mPaint;

    public DefaultListItemDecoration() {
        mPaint = new Paint();
        mPaint.setColor(BACKGROUND_COLOR);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        Rect rect=new Rect();
        for(int i=0;i<parent.getChildCount();i++){
            View view =parent.getChildAt(i);
            rect.left=view.getLeft();
            rect.right=view.getRight();
            rect.top=view.getTop();
            rect.bottom=view.getBottom();
            c.drawRect(rect,mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = DIVIDER_HEIGHT;
    }
}

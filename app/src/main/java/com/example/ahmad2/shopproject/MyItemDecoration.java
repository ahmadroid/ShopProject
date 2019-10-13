package com.example.ahmad2.shopproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class MyItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private int orientation;
    private Drawable divider;
    private static int[] attr = {android.R.attr.listDivider};

    public MyItemDecoration(Context context, int orientation) {
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attr);
        divider = typedArray.getDrawable(0);
        typedArray.recycle();
        checkOrientation(orientation);
    }

    private void checkOrientation(int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("error argument");
        } else {
            this.orientation = orientation;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
        }
        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            drawVerticalDivider(c, parent);
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            drawHorizontalDivider(c, parent);
        }
        super.onDrawOver(c, parent, state);
    }

    private void drawHorizontalDivider(Canvas canvas, RecyclerView recycle) {
        int top = recycle.getPaddingTop();
        int bottom = recycle.getHeight() - recycle.getPaddingBottom();
        for (int i = 0; i < recycle.getChildCount(); i++) {
            View view = recycle.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            int left = view.getRight() + params.rightMargin;
            int right = left + divider.getIntrinsicWidth();
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    private void drawVerticalDivider(Canvas canvas, RecyclerView recycle) {
        int left = recycle.getPaddingLeft();
        int right = recycle.getWidth() - recycle.getPaddingRight();
        for (int i = 0; i < recycle.getChildCount(); i++) {
            View view = recycle.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            int top = view.getBottom() + params.bottomMargin;
            int bottom=top+divider.getIntrinsicHeight();
            divider.setBounds(left,top,right,bottom);
            divider.draw(canvas);
        }
    }
}

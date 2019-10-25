package ello.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category SpacesItemDecoration
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/*****************************************************************
 SpacesItemDecoration
 ****************************************************************/
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int halfSpace;

    public SpacesItemDecoration(int space) {
        this.halfSpace = space / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getPaddingLeft() != halfSpace) {
            parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
            parent.setClipToPadding(false);
        }

        outRect.top = halfSpace;
        outRect.bottom = halfSpace;
        outRect.left = halfSpace;
        outRect.right = halfSpace;
    }
}
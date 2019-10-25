package com.obs;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

/**
 * Created by divyas on 6/24/2015.
 */
public class CustomCheckBox extends AppCompatCheckBox {
    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            FontCustomTextViewHelper.initialize(this, context, attrs);
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int w;
//        if (widthMeasureSpec < heightMeasureSpec) {
//            w = widthMeasureSpec;
//        } else {
//            w = heightMeasureSpec;
//        }
//        super.onMeasure(w, w);
//    }
}

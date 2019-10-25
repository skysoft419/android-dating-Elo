package com.obs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * CustomTextView TextView widget with a typeface done directly using style.
 */
public class CustomEditText extends AppCompatEditText {
private Context context;
    public CustomEditText(Context context, AttributeSet attrs) {

        super(context, attrs);
         {
             this.context=context;
            FontCustomTextViewHelper.initialize(this, context, attrs);
        }
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }

}

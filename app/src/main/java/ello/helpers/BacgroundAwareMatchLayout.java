package ello.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import ello.R;

public class BacgroundAwareMatchLayout extends RelativeLayout {
    private int childId=0;
    private View childView;
    private Paint eraser;
    private float radius=0;
    private RectF childRect=new RectF();
    public BacgroundAwareMatchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(attrs,context);

    }

    public BacgroundAwareMatchLayout(Context context) {
        super(context);
    }

    public BacgroundAwareMatchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(attrs,context);
    }

    private void setUp(AttributeSet attrs, Context context){
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.BackgroundAwareLayout);
        this.childId=ta.getResourceId(R.styleable.BackgroundAwareLayout_child_id,0);
        this.radius= (float) context.getResources().getDimensionPixelSize(R.dimen.onboarding_button_radius);
        if (childId != 0){
            ta.recycle();
            setupEraser(context);
            return;
        }
    }
    private void setupEraser(Context context){
        eraser = new Paint();
        eraser.setColor(ContextCompat.getColor(context, android.R.color.transparent));
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setAntiAlias(true);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child.getId() == childId){
            this.childView=child;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        childRect.set((float)childView.getLeft(), (float)childView.getTop(),
                (float)childView.getRight(), (float)childView.getBottom());
        canvas.drawRoundRect(childRect, radius, radius, eraser);
    }
}
